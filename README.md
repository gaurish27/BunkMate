# 📚 BunkMate — Smart Attendance & Timetable Manager

**BunkMate** is an Android application designed for students to efficiently manage their class attendance, weekly timetables, and safe bunks.  
It helps users keep track of attendance percentages, calculate safe bunks dynamically based on their timetable, and mark attendance easily for daily classes.

---

## 🚀 Features

- 📅 **Weekly Timetable Management** — Add, edit, or remove class periods for each weekday.
- 🧑‍🏫 **Attendance Tracker** — Mark attendance as *Present*, *Absent*, or *Cancelled* with one tap.
- 📊 **Smart Safe Bunk Calculation** — Calculates monthly safe bunks per subject based on:
  - Minimum required attendance %
  - Current attendance record
  - Weekly class frequency
- 📈 **Performance Dashboard** — View subject-wise progress with percentage indicators.
- 🔒 **Local Database (SQLite)** — Works completely offline.
- 👤 **User Registration & Auto Login** — Simple local user management using SharedPreferences.

---

## 🛠️ Tech Stack

| Component | Technology |
|------------|-------------|
| **Frontend (UI)** | XML layouts, Material Components |
| **Backend (Local)** | SQLite with custom DAOs |
| **Architecture** | MVC (Model–View–Controller) |
| **Language** | Java |
| **IDE** | Android Studio |

---

---

## 💾 Database Schema (SQLite)

- **users** — Stores user credentials  
- **subjects** — Subject details (name, min % required, etc.)  
- **timetable** — Weekly class schedule  
- **attendance** — Daily attendance logs (linked to subjects)

---

## 🧮 Safe Bunk Calculation

Safe bunks per month are calculated dynamically using the formula:

\[
\text{Safe Bunks} = \text{floor}\left(\frac{(A + M) \times 100}{R} - (T + M)\right)
\]

Where:
- `A` = Attended classes  
- `T` = Total classes  
- `R` = Required attendance %  
- `M` = Weekly classes × 4 (approx. per month)

---

## 🧭 Usage Guide

1. Register a new account or auto-login.
2. Add subjects with minimum required attendance %.
3. Create a weekly timetable with periods per day.
4. Mark attendance daily via the **Event Fragment**.
5. Check **Home Fragment** for subject stats and monthly safe bunks.

---

## 🧩 Future Enhancements

- Cloud backup for attendance data  
- Dark mode  
- Subject-wise analytics charts  
- Semester summary export (PDF/CSV)

---

## 👨‍💻 Author

**Gaurish Jariwala**  
📧 [email@example.com]  
💻 Developed as part of MCA Project  
🏫 Jain University

---

## 🪪 License

This project is open-source and licensed under the [MIT License](LICENSE).


