# Days Left — System Architecture Documentation

**Package:** `com.daysleft`  
**Platform:** Android (API 26+)  
**UI Framework:** Jetpack Compose (Material 3)  
**Database:** Room (SQLite)  
**Architecture Pattern:** Clean Architecture (MVVM + Repository + Unidirectional Data Flow)  

---

## 1. Architectural Blueprint

```
+-----------------------------------------------------------------------+
|                             Presentation Layer                        |
|                                                                       |
|   HomeScreen         AddEventScreen     EditEventScreen   DetailsScreen |
|        |                  |                  |                  |     |
|   HomeViewModel      AddEventViewModel  EditEventViewModel DetailsVM  |
|        \                  |                  |                 /      |
+---------\-----------------+------------------+----------------/-------+
           \                |                  |               /
            v               v                  v              v
+-----------------------------------------------------------------------+
|                              Data Layer                               |
|                                                                       |
|                          EventRepository                              |
|                                 |                                     |
|                             EventDao                                  |
|                                 |                                     |
|                           AppDatabase                                 |
|                             (Room)                                    |
+-----------------------------------------------------------------------+
                                  ^
                                  |
+---------------------------------+-------------------------------------+
|                         Reminders & System                            |
|                                                                       |
|   ReminderScheduler ----> AlarmManager (RTC_WAKEUP)                   |
|          |                        |                                   |
|          |                        v                                   |
|          |                 ReminderReceiver ----> System Notification |
|          |                                            |               |
|          +--- BootAndAlarmReceiver <------------------+ (Deep Link)   |
|               (Boot / Timezone / Package Update)                      |
+-----------------------------------------------------------------------+
```

---

## 2. Layer Responsibilities

### 2.1 Application & Dependency Injection (`com.daysleft.di`, `com.daysleft`)
- **`DaysLeftApplication`**: The Android application entry point. Initializes `AppContainer` and registers the notification channel on process startup.
- **`AppContainer`**: A lightweight, deterministic service locator managing singletons:
  - `database: AppDatabase`
  - `repository: EventRepository`
  - `reminderScheduler: ReminderScheduler`
- **ViewModel Factories**: ViewModels retrieve dependencies cleanly from `CreationExtras[APPLICATION_KEY] as DaysLeftApplication`.

### 2.2 Data Layer (`com.daysleft.data`)
- **`Event` Entity**: Room data entity representing a countdown event:
  - `id: Long`: Auto-incrementing primary key.
  - `title: String`: User event title.
  - `date: LocalDate`: Event target date.
  - `remindersEnabled: Boolean`: Master toggle for reminders.
  - `remindSevenDaysBefore`, `remindOneDayBefore`, `remindOnDay`: Interval toggles.
  - `reminderHour`, `reminderMinute`: Time of day for reminders.
- **`EventDao`**: Room data access object providing both reactive `Flow<List<Event>>` and direct suspend functions.
- **`Converters`**: TypeConverter transforming `LocalDate` to ISO-8601 strings (`YYYY-MM-DD`).
- **`AppDatabase`**: SQLite database with schema version 2 and `MIGRATION_1_2` safely upgrading existing v1 tables.
- **`EventRepository`**: Clean interface and `EventRepositoryImpl` abstracting database queries from the presentation layer.

### 2.3 Presentation Layer (`com.daysleft.ui`)
- **Unidirectional Data Flow (UDF)**:
  - ViewModels hold state in `MutableStateFlow` and expose read-only `StateFlow<UiState>`.
  - Composables render state and emit user events/intents as lambda callbacks.
  - State mutations are atomic via `_uiState.update { ... }`.
- **Compose Design System (`com.daysleft.ui.theme`)**:
  - `Color.kt`: Official Days Left website color palette (Brand `#2D52C8`, container `#4A6CE2`, dark surface `#1A1B22`, dark background `#121318`).
  - `Spacing.kt`: CompositionLocal provider `LocalSpacing` for consistent padding tokens.
  - `Shape.kt`: Tokenized component corner radii (`Card = 16.dp`, `TextField = 12.dp`, `Dialog = 20.dp`, `Pill = CircleShape`).
  - `Type.kt`: Material 3 typography with custom scales for countdown numbers.

### 2.4 Reminders & Alarm Subsystem (`com.daysleft.reminder`)
- **`ReminderType`**: Enum representing intervals (7 days, 1 day, on the day) with offset IDs for deterministic request codes:
  $$\text{RequestCode} = (\text{EventId} \times 10) + \text{IdOffset}$$
- **`ReminderTimeCalculator`**: Calendar-aware date/time calculations that account for month lengths, leap days, and time zones. Prevents scheduling alarms for past timestamps.
- **`ReminderScheduler`**: Interacts with `AlarmManager.setExactAndAllowWhileIdle()`, gracefully falling back to standard alarms if exact alarm permission is absent.
- **`ReminderReceiver`**: Receives broadcast alarms (`android:exported="false"`) and publishes notifications with deep-link PendingIntents to `MainActivity`.
- **`BootAndAlarmReceiver`**: Listens for system boot (`ACTION_BOOT_COMPLETED`), package update (`ACTION_MY_PACKAGE_REPLACED`), and timezone/time changes (`ACTION_TIMEZONE_CHANGED`, `ACTION_TIME_SET`, `ACTION_DATE_CHANGED`) to reschedule all active alarms asynchronously.

---

## 3. Data Flow & Lifecycles

1. **User Creates/Edits Event**:
   - UI emits form data to `AddEventViewModel` / `EditEventViewModel`.
   - ViewModel validates input via `EventValidator`.
   - ViewModel writes event to `EventRepository.insertEvent()` / `updateEvent()`.
   - ViewModel calls `ReminderScheduler.scheduleEventReminders()` to sync alarms.
   - Room updates SQLite database and triggers downstream emissions to `HomeViewModel.events` and `EventDetailsViewModel.uiState`.

2. **Notification Alarm Trigger**:
   - `AlarmManager` fires at scheduled epoch timestamp.
   - `ReminderReceiver.onReceive()` validates payload and creates rich notification.
   - User taps notification $\rightarrow$ launches `MainActivity` with `EXTRA_EVENT_ID` $\rightarrow$ deep-links straight to `EventDetailsRoute(eventId)`.
   - If event was deleted in the meantime, `EventDetailsScreen` gracefully presents a "Countdown not found" fallback without crashing.
