# Network Analyzer - Android Cybersecurity Application

تطبيق أندرويد متكامل للأمن السيبراني والتحليل الشبكي مبني بأحدث تقنيات **Kotlin** و **Jetpack Compose**.

## 🚀 المميزات الرئيسية (Core Features)

- **اكتشاف الأجهزة والشبكة (ARP Device Discovery)**:
  - فحص أجهزة الشبكة المحلية وقراءة جدول كاش النواة `/proc/net/arp`.
  - كشف عناوين الـ IP و الـ MAC ومصنعي الأجهزة عبر OUI.
- **خريطة طوبولوجيا الشبكة التفاعلية (Network Topology Map)**:
  - عرض تفاعلي لمخططات النجمة (Star)، الشجرة (Tree)، والحلقة (Ring).
- **محلل الحزم الشبكية (Live Packet Sniffer & Analyzer)**:
  - تتبع حزم البيانات المارة، مصادرها، وجهاتها، وبروتوكولاتها (TCP, UDP, ICMP, DNS, TLS).
- **فحص الثغرات والمنافذ (Vulnerability & Port Scanner)**:
  - فحص المنافذ المفتوحة وتقييم مستويات الخطورة (Critical, High, Medium, Low).
- **التدقيق الأمني اللاسلكي (Wireless Security Audit)**:
  - فحص نوع التشفير (WPA2/WPA3)، كشف شبكات Evil Twin الخبيثة، وحماية PMF.
- **توليد التقارير الأمنية (Security Reports)**:
  - استخراج تقارير تنفيذية مفصلة مع دعم التصدير بصيغة CSV والنص المنسق.

---

## 🛠️ متطلبات البناء والتشغيل (Build Requirements)

- **Android Studio** (Ladybug / Iguana أو أحدث)
- **JDK 17** (Java Development Kit)
- **Android SDK Platform 36** (Min SDK: 24, Target SDK: 36)
- **Gradle 9.3.1+** (مرفق تلقائياً عبر Gradle Wrapper)

---

## 📦 البناء التلقائي عبر GitHub Actions (CI/CD)

تم تجهيز المشروع بملف سير العمل المعتمد والمحدث:
- `.github/workflows/android.yml`

بمجرد رفع المشروع إلى GitHub:
1. سيعمل **GitHub Actions** تلقائياً لبناء التطبيق على بيئة نظام Ubuntu مع JDK 17 و Android SDK Platform 36 و Gradle 9.3.1.
2. عند اكتمال البناء، ستجد ملف **`app-debug.apk`** جاهزاً للتحميل والتثبيت المباشر في تبويب **Actions** -> قسم **Artifacts** باسم `app-debug-apk`.

---

## 💻 البناء يدوياً عبر سطر الأوامر (Local Build)

إذا قمت بتحميل المشروع إلى جهازك:

### نظام Linux / macOS:
```bash
chmod +x gradlew
./gradlew assembleDebug
```

### نظام Windows:
```cmd
gradlew.bat assembleDebug
```

سيتوفر ملف الـ APK المجمّع في المسار التالي:
`app/build/outputs/apk/debug/app-debug.apk`
