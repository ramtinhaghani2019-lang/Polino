# Polino Android — 0.1.1-alpha

نسخه اولیه قابل توسعه اپ مدیریت مالی «پولینو».

## امکانات فعلی
- رابط فارسی و RTL با Jetpack Compose
- داشبورد اولیه
- ثبت دستی هزینه
- ذخیره‌سازی با Room
- واحد پایه داخلی: ریال ایران (IRR)
- ورود مبلغ به ریال یا تومان و نرمال‌سازی به ریال
- اسکلت دریافت پیامک بانکی و Parser اولیه
- تست واحد تبدیل پول و Parser

## ابزارهای Build
- Android Gradle Plugin: 9.3.0
- Gradle: 9.5.0
- compileSdk / targetSdk: 37 (Android 17)
- minSdk: 26
- JDK: 17 یا بالاتر سازگار با Android Studio

## اجرای پروژه
1. پروژه را با Android Studio باز کنید.
2. Android SDK Platform 37 و Build Tools موردنیاز را از SDK Manager نصب کنید.
3. اجازه دهید Gradle Sync کامل شود.
4. یک دستگاه/Emulator اندروید 8+ انتخاب و Run کنید.

### Build از خط فرمان
Windows:
```
gradlew.bat test assembleDebug
```
macOS/Linux:
```
./gradlew test assembleDebug
```

اگر `gradle-wrapper.jar` داخل ZIP موجود نباشد، اسکریپت‌های gradlew در اولین اجرا آن را از مخزن رسمی Gradle روی GitHub دریافت می‌کنند.

APK پس از Build معمولاً در این مسیر است:
`app/build/outputs/apk/debug/app-debug.apk`

## نکته SMS
قابلیت SMS هنوز Alpha است و مجوزها باید در زمان اجرا از کاربر گرفته شوند. قبل از انتشار عمومی، سیاست‌های مارکت درباره READ_SMS/RECEIVE_SMS باید بررسی و رعایت شود.

## GitHub Actions APK build

This project includes `.github/workflows/android-build.yml`.

After pushing the project to GitHub, open **Actions → Build Polino Android → Run workflow** (or simply push to `main`, `master`, or `develop`). The workflow runs unit tests, builds `app-debug.apk`, and uploads it as the **Polino-debug-apk** artifact.

The workflow can also recreate `gradle-wrapper.jar` automatically with Gradle 9.5.0 if that binary is not present in the repository.
