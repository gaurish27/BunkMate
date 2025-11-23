# 📚 BunkMate — Smart Attendance & Timetable Manager

**BunkMate** is an Android application designed for students to efficiently monitor their class attendance, manage weekly timetables, and calculate safe bunks intelligently.  
It helps track attendance percentages, automatically determines how many lectures a student can safely skip, and offers a clean UI to mark daily attendance.

---

## 🚀 Features

- 📅 **Weekly Timetable Management** — Add, edit, or remove class periods for each weekday.
- 🧑‍🏫 **Easy Attendance Marking** — Present, Absent, Cancelled with one tap.
- 📊 **Smart Safe Bunk Calculation**  
  Based on:
  - Minimum required attendance %
  - Current attendance record  
  - Weekly class frequency  
- 📈 **Subject Dashboard** — Displays percentages, safe bunks & progress.
- 🔒 **Works Fully Offline** — Powered by SQLite local DB.
- 👤 **User Registration & Auto Login** — Managed with SharedPreferences.

---

## 🛠️ Tech Stack

| Component | Technology |
|----------|------------|
| UI | XML + Material Components |
| Local Database | SQLite |
| Architecture | MVC |
| Language | Java |
| IDE | Android Studio |

---

## 💾 Database Schema

- **users** — User authentication  
- **subjects** — Subject details  
- **timetable** — Weekly timetable structure  
- **attendance** — Daily logs for each class  

---

## 🧮 Safe Bunk Formula

\[
\text{Safe Bunks} = \text{floor}\left(\frac{(A + M) \times 100}{R} - (T + M)\right)
\]

Where:  
- **A** = Attended  
- **T** = Total  
- **R** = Required %  
- **M** = (Weekly classes × 4 weeks)

---

## 🧭 Usage Guide

1. Register a user or auto-login.
2. Add subjects & required attendance.
3. Create timetable for each weekday.
4. Mark attendance daily (Present/Absent/Cancelled).
5. Check dashboard to see safe bunks & progress.

---

## 🖼️ Screenshots

### 🔐 Login & Register  
<table>
<tr>
<td><img src="https://github.com/user-attachments/assets/9d21c12e-e2c2-45cd-bdf2-3390e6e3a264" width="280"></td>
<td><img src="https://github.com/user-attachments/assets/08a9d271-bad2-4e64-8efd-7fcd26297d01" width="280"></td>
</tr>
</table>

### 🏠 Home & Attendance  
<table>
<tr>
<td><img src="https://github.com/user-attachments/assets/9d080f11-c2f4-466f-8c8c-c30de4c20975" width="280"></td>
<td><img src="https://github.com/user-attachments/assets/96101475-c8bd-4bfa-b485-1bd4a49e1b40" width="280"></td>
</tr>
</table>

### 👤 Profile & Timetable  
<table>
<tr>
<td><img src="https://github.com/user-attachments/assets/6a5d5275-6b06-416f-9504-39b591475d2e" width="280"></td>
<td><img src="https://github.com/user-attachments/assets/224de95f-6092-4f0e-94ca-a0ee3b795b27" width="280"></td>
</tr>
</table>

---

## 🧩 Future Enhancements

- Cloud backup  
- Dark mode  
- Subject analytics charts  
- Export attendance summary (PDF/CSV)

---

## 👨‍💻 Authors

**Gaurish Jariwala**  
**Ayush Lakhani**  
**Manasa Hegde**  
📚 MCA Project — Jain University  

---

## 🪪 License  
This project is licensed under the **MIT License**.
