# Arogya-Sahaya Local 🩺

**Android App Development using GenAI - Healthcare**  
*MindMatrix VTU Internship Program | Project Title: 78*

Arogya-Sahaya Local is a localized "Digital Health Companion" for rural elderly users. The app simplifies medication schedules and health check tracking for users who may not be tech-savvy, aiming for "Zero-Error" medicine intake and better follow-ups.

## 🎯 Problem Statement

In rural areas, elderly patients often miss healthcare follow-ups because they struggle to manage multiple medications and remember health camp dates or ASHA worker visits. This leads to poor health outcomes and preventable complications.

## ✨ Key Features

- **Medical Profile**: Store age, chronic conditions, and basic health info
- **Pill Reminder**: Simple interface to add medicine name, dosage, and times (Morning/Afternoon/Night)
- **ASHA Connect**: Calendar showing local health camp dates 
- **Vital Log**: Track BP, glucose, heart rate with 7-day trend graphs
- **Emergency Mode**: Large "SOS" button for simulated emergency call/message
- **Offline-First**: All data stored locally for rural connectivity

## 📱 App Flow

1. **Onboarding**: User enters basic medical profile
2. **Set Reminders**: Add medications with time-based schedules 
3. **Log Vitals**: Enter daily BP/glucose readings
4. **View Trends**: Check 7-day line charts before ASHA visits
5. **Check Calendar**: See upcoming health camp dates

## 🛠 Tech Stack

- **Language**: Kotlin
- **Architecture**: Repository Pattern
- **Database**: Room DB for local medication + vital logs
- **Reminders**: WorkManager/AlarmManager for Doze mode compatibility
- **Charts**: MPAndroidChart library for 7-day trend visualization
- **UI/UX**: High-contrast colors, large fonts for elderly accessibility

## 🎯 Impact Goals

- **Health Inclusion**: Help elderly in remote areas maintain medication adherence
- **Data-Driven Care**: Give ASHA workers organized patient history during visits
- **Preventative Health**: Reduce emergency hospitalizations through consistent monitoring

## ✅ Success Criteria

- [ ] Notifications trigger accurately even when device is in "Doze" mode
- [ ] Vital Log generates 7-day trend graph successfully
- [ ] UI uses high-contrast colors and large fonts for accessibility
- [ ] Implements Repository Pattern for data handling

## 🚀 Getting Started

1. **Clone the repo**
   ```bash
   git clone https://github.com/your-username/Arogya-Sahaya-Local.git
