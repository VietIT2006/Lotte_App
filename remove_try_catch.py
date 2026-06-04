import os
import glob

directory = r'C:\LapTrinhHuongDichVu\Lotte_App\app\src\main\java\com\ptithcm\lottemart\features\admin'
activities = glob.glob(os.path.join(directory, 'Admin*Activity.java'))

for filepath in activities:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    if 'try {' not in content or 'Crash Detected' not in content:
        continue

    # Remove the catch block
    catch_block = '''
        } catch (Exception e) {
            e.printStackTrace();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Crash Detected");
            builder.setMessage(android.util.Log.getStackTraceString(e));
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    '''
    
    content = content.replace(catch_block, '    }')
    content = content.replace('super.onCreate(savedInstanceState);\n        try {', 'super.onCreate(savedInstanceState);')

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

print('Restored Activities.')
