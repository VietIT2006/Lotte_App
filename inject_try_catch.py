import os
import glob

directory = r'C:\LapTrinhHuongDichVu\Lotte_App\app\src\main\java\com\ptithcm\lottemart\features\admin'
activities = glob.glob(os.path.join(directory, 'Admin*Activity.java'))

for filepath in activities:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    if 'try {' in content and 'catch (Exception e)' in content:
        continue

    start_str = 'protected void onCreate(Bundle savedInstanceState) {'
    if start_str not in content:
        continue
    
    parts = content.split(start_str)
    before_onCreate = parts[0]
    rest = parts[1]
    
    if 'super.onCreate(savedInstanceState);' not in rest:
        continue
        
    rest = rest.replace('super.onCreate(savedInstanceState);', 'super.onCreate(savedInstanceState);\n        try {', 1)
    
    last_brace_index = rest.rfind('}')
    second_last_brace_index = rest.rfind('}', 0, last_brace_index)
    
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
    
    rest = rest[:second_last_brace_index] + catch_block + rest[second_last_brace_index:]
    
    new_content = before_onCreate + start_str + rest
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(new_content)

print('Instrumented Activities with Crash Handler.')
