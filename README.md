# GABRA - Smart Delivery Auto-Accept Bot

تطبيق ذكي يعمل في الخلفية على أجهزة Android ويهدف إلى أتمتة عملية قبول الطلبات في تطبيقات التوصيل بناءً على موقع السائق.

## المتطلبات

- Android Studio Giraffe أو أحدث
- JDK 17 أو أحدث
- Android SDK 34
- Android Gradle Plugin 8.1.0
- Gradle 8.5

## إعداد المشروع

1. تثبيت المتطلبات:
   - تثبيت JDK 17 من: https://adoptium.net/temurin/releases/?version=17
   - تثبيت Android Studio من: https://developer.android.com/studio
   - تثبيت Android SDK 34

2. إعداد المتغيرات البيئية:
   - إضافة JAVA_HOME إلى المتغيرات البيئية:
     ```
     JAVA_HOME=C:\Program Files\Java\jdk-17
     ```

3. فتح المشروع:
   - افتح Android Studio
   - اختر "Open an Existing Android Studio Project"
   - انتقل إلى مجلد المشروع: `C:\Users\admin\CascadeProjects\gabra`
   - انتظر حتى يتم تزامن المشروع

4. تشغيل التطبيق:
   - اضغط على زر التشغيل (الزر الأخضر) أو اضغط Shift+F10
   - سيتم تثبيت التطبيق على جهاز Android المتصل أو المحاكي

## استخدام التطبيق

1. قم بتشغيل التطبيق
2. امنح الصلاحيات المطلوبة:
   - صلاحيات الوصول إلى الشاشة
   - صلاحيات الموقع
3. قم بتفعيل خدمة GABRA من إعدادات Android
4. استخدم الأزرار في لوحة التحكم:
   - تشغيل/إيقاف الخدمة
   - تفعيل وضع الاختبار
5. شاهد سجل العمليات في نافذة السجل
