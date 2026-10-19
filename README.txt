# Cash Manager Android App

## Build APK in Android Studio
1. Install Android Studio on a laptop/PC.
2. Open Android Studio -> Open, and choose this `CashManagerAndroid` folder.
3. Let Gradle sync finish. If prompted, install Android SDK Platform 35 and Build Tools.
4. Choose Build -> Build Bundle(s) / APK(s) -> Build APK(s).
5. When complete, click "locate" in the notification. The debug APK is normally at:
   `app/build/outputs/apk/debug/app-debug.apk`
6. Transfer the APK to your Android phone, open it, and allow installation from that source if Android asks.

## Features
- Fully offline Android WebView app.
- Opening balance and continuous running balance.
- Received and Paid transactions with date, person and description.
- Transaction history, edit/delete and CSV export.
- Data stored locally in WebView localStorage.

## Important
- This project source is provided; an APK has not been compiled in this environment.
- Export CSV regularly. Uninstalling the app or clearing its storage may delete locally saved records.
- Do not set a new opening balance every day. The running balance automatically carries forward.

## Build APK through GitHub without Android Studio
1. Create a GitHub repository and upload all project files, including the hidden `.github/workflows/build-apk.yml` file.
2. Make sure the default branch is named `main`.
3. Open the repository on GitHub and select Actions.
4. Select "Build Cash Manager APK" and click "Run workflow" (or push a commit to main).
5. When the workflow finishes successfully, open its run and download the `CashManager-debug-apk` artifact.
6. Extract the downloaded artifact ZIP to get `app-debug.apk`, then transfer it to your Android phone and install it.

Note: GitHub Actions builds a debug APK for testing. This is not a Play Store release-signed APK.

## Latest update
- Date and time are captured in the transaction form and shown in transaction history.
- For Paid transactions, both the recipient (who was paid) and reason/purpose are required.
- CSV export includes date/time, recipient/person, and reason/purpose.

## Transaction details (updated)
- Date and time are captured automatically by default and displayed in transaction history.
- For both Received and Paid, the person/from-to and reason/purpose fields are required.
- CSV export includes date/time, person/from-to, and reason/purpose.

## Six-month history and filters (updated)
- The app automatically prunes transactions older than six months.
- History filters: from date, to date, transaction type (Received/Paid), person/name, and reason/details.
- Date/time remains visible on every matching transaction, along with the running balance after it.
- CSV export follows the currently applied filters.

## Print / PDF history (updated)
- Use the Print / Save PDF button in Transaction History.
- It prints the currently filtered list. Use Show All (6 months) before printing all retained history.
- In the Android print dialog, select "Save as PDF" if available, or print to a connected printer.

## Cash count / denomination calculator (updated)
- Enter quantity for ₹500, ₹200, ₹100, ₹50, ₹20, ₹10, ₹5, ₹2 and ₹1 notes/coins.
- Total cash counted is calculated automatically.
- Available balance is calculated from opening balance plus received minus paid.
- Difference = Total Cash Counted minus Available Balance. Positive means extra cash; negative means cash short.
