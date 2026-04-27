# WiFi Guard — Native Android (Java)

اپ بومی اندروید برای اسکن شبکه‌های WiFi و تشخیص شبکه‌های جعلی (Evil Twin)، نوشته شده با **جاوا** و Material 3.

## ویژگی‌ها

- اسکن واقعی WiFi با `WifiManager.startScan()`
- تشخیص Evil Twin (نام تکراری با امنیت متفاوت + فاصله Levenshtein ≤ ۱ + نرمال‌سازی هم‌نگاشت)
- محاسبه ریسک ۰..۱۰۰ بر اساس نوع امنیت، پنهان‌بودن و جعلی‌بودن
- تحلیل قدرت رمز عبور (آنتروپی، زمان شکستن، رمزهای رایج)
- تاریخچه ۵۰ اسکن آخر در `SharedPreferences`
- رابط فارسی RTL کامل (مقدار پیش‌فرض locale = fa)
- فال‌بک خودکار به داده شبیه‌سازی‌شده وقتی مجوز نیست یا نتیجه خالی است (مناسب امولاتور)

## باز کردن در Android Studio

1. Android Studio Hedgehog یا جدیدتر را باز کنید
2. `File → Open` و پوشه `WifiGuard` را انتخاب کنید
3. منتظر همگام‌سازی Gradle بمانید
4. روی Run کلیک کنید (Android 8.0 / API 26 یا بالاتر)

## ساخت APK دستی

```bash
./gradlew assembleDebug
# خروجی: app/build/outputs/apk/debug/app-debug.apk
```

## مجوزها

- `ACCESS_FINE_LOCATION` (الزامی برای دیدن نتایج اسکن در Android 8+)
- `ACCESS_WIFI_STATE`, `CHANGE_WIFI_STATE`
- `NEARBY_WIFI_DEVICES` (Android 13+)

## ساختار پروژه

```
app/src/main/
├── java/com/wifiguard/app/
│   ├── MainActivity.java
│   ├── model/         (WifiNetwork, ScanSession, Security, PasswordAnalysis)
│   ├── scan/          (WifiScanner, EvilTwinDetector)
│   ├── security/      (PasswordAnalyzer)
│   ├── data/          (ScanHistoryStore)
│   ├── util/          (OuiLookup)
│   └── ui/            (Fragments, Adapters, RadarView, RiskGaugeView, ...)
└── res/
    ├── layout/, values/, values-fa/, drawable/, menu/, mipmap-anydpi-v26/
```
