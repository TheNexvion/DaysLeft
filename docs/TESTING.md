# Days Left — Test Strategy & Verification Guide

**Project:** Days Left Android App  
**Test Suite:** Unit Tests (JVM) + Instrumented Tests (Android Device/Emulator)  

---

## 1. Overview

The Days Left test suite verifies core business logic, date calculations, reminder/alarm mechanics, Room database operations, and ViewModel state transitions without relying on external network connectivity or cloud mocks.

---

## 2. Test Structure

```
app/src/test/java/com/daysleft/
├── MainDispatcherRule.kt                # Coroutine TestRule replacing Dispatchers.Main with TestDispatcher
├── data/
│   ├── FakeEventDao.kt                  # In-memory reactive Fake DAO for rapid ViewModel testing
│   └── EventRepositoryTest.kt           # Tests for repository CRUD operations & Flow emissions
├── reminder/
│   └── ReminderTimeCalculatorTest.kt    # Tests for reminder math, past date skipping, & request codes
├── ui/
│   ├── home/HomeViewModelTest.kt        # Tests for HomeViewModel StateFlow & ordering
│   ├── add/AddEventViewModelTest.kt     # Tests for event creation, input validation, & error clearing
│   ├── edit/EditEventViewModelTest.kt   # Tests for dirty tracking, update, & deletion
│   └── details/EventDetailsVMTest.kt    # Tests for observing event & deletion
└── util/
    └── DateUtilsTest.kt                 # Countdown calculations, formatting, presets, & leap years

app/src/androidTest/java/com/daysleft/
└── data/local/
    └── EventDaoTest.kt                  # Real SQLite Room in-memory database test
```

---

## 3. Running Automated Tests

### 3.1 Unit Tests (Command Line)
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat testDebugUnitTest --no-daemon
```

### 3.2 Clean & Full Verification
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat clean testDebugUnitTest assembleDebug --no-daemon
```

---

## 4. Key Test Coverage Areas

| Component | Tested Scenarios |
|---|---|
| **DateUtils** | Positive days until, today = 0, past negative days, leap year February 28/29, year boundary crossing (Dec 31 $\rightarrow$ Jan 1), 4 status categories (Urgent, Upcoming, Today, Passed), presets (Tomorrow, Weekend, 1 Week, 1 Month). |
| **ReminderTimeCalculator** | Future reminders (7d, 1d, 0d), skipping passed reminders (e.g. 3-day event skips 7d), same-day reminder before/after target hour, individual reminder toggle combinations, deterministic request code formulas. |
| **EventRepository** | `insertEvent`, `updateEvent`, `deleteEvent`, `deleteEventById`, `getAllEvents` flow emissions, `getEventById` reactive updates. |
| **HomeViewModel** | Initial empty state, reactive updates upon insertion, chronological ordering by target date. |
| **AddEventViewModel** | Initial template title setting, input validation (blank title, null date), error state clearing on text input, successful save state transition. |
| **EditEventViewModel** | Loading initial event data, dirty tracking across title/date/reminder edits, clean dirty state when reverting, updating event, deleting event. |
| **EventDetailsViewModel** | Observing event data, handling deletion. |

---

## 5. Smoke Testing Checklist (Device / Emulator)

- [x] Launch app from launcher
- [x] Home screen renders empty state when no events exist
- [x] Tap template chip (e.g., "Birthday") $\rightarrow$ opens Add screen with title prefilled
- [x] Fill event name and target date $\rightarrow$ tap "Create Countdown" $\rightarrow$ event appears on Home list
- [x] Tap event card $\rightarrow$ opens Event Details screen with Countdown Hero and target date
- [x] Tap "Edit Countdown" $\rightarrow$ modify title and toggle reminders $\rightarrow$ tap "Save Changes" $\rightarrow$ changes reflected
- [x] Delete event $\rightarrow$ event removed from Home screen
- [x] Switch system Dark Mode / Light Mode $\rightarrow$ colors update cleanly with official branding
- [x] Trigger notification alarm $\rightarrow$ notification appears in shade
- [x] Tap notification $\rightarrow$ opens app directly into the event's detail screen
