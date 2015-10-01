import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Comparison {

	public static void main(String[] args) throws ClassNotFoundException, MalformedURLException {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter full path to newest file");
		//String newFile = input.next();
		String newFile = "C:\\Users\\gtkamin\\Modified\\ReboundPanel.java";
		System.out.println("Enter full path to old file");
		//String oldFile = input.next();
		String oldFile = "C:\\Users\\gtkamin\\ReboundPanel.java";
		System.setProperty("java.home", "C:\\Program Files (x86)\\Java\\jdk1.8.0_45");
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int firstCompilationResult =	compiler.run(System.in, System.out, System.err, newFile);
		if(firstCompilationResult == 0){
			System.out.println("First Compilation is successful");
		}else{
			System.out.println("Compilation Failed");
		}
		int secondCompilationResult =	compiler.run(null, null, null, oldFile);
		if(secondCompilationResult == 0){
			System.out.println("First Compilation is successful");
		}else{
			System.out.println("Compilation Failed");
		}
		if (firstCompilationResult == 0 && secondCompilationResult == 0){
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			File fileNew = new File(newFile);
			File fileOld = new File(oldFile);
			URL[] firstURL = {fileNew.getParentFile().toURL()};
			URL[] secondURL =  {fileOld.getParentFile().toURL()};
			URLClassLoader ucl = new URLClassLoader(firstURL);
			
			//Class newClass = classLoader.loadClass(newFile.replace(".java", ".class"));
			//Class oldClass = classLoader.loadClass(oldFile.replace(".java", ".class"));
			
			Class newClass = ucl.loadClass(fileNew.getName().replace(".java", ""));
			
			ucl = new URLClassLoader(secondURL);
			Class oldClass = ucl.loadClass(fileOld.getName().replace(".java", ""));
			System.out.println("AM: " + AddMethod(newClass, oldClass));
			System.out.println("DM: " + DeleteMethod(newClass, oldClass));
			System.out.println("CM: " + ChangeMethod(newClass, oldClass));
			System.out.println("AF: " + AddField(newClass, oldClass));
			System.out.println("DF: " + DeleteField(newClass, oldClass));
			System.out.println("CFI: " + ChangeFieldInit(newClass, oldClass));
		}
	}
	
	public static String AddMethod(Class newClass, Class oldClass){
		return "";
	}
	
	public static String DeleteMethod(Class newClass, Class oldClass){
		Method[] newMethods = newClass.getDeclaredMethods();
		Method[] oldMethods = oldClass.getDeclaredMethods();
		String resultString = "";
		for (Method method : oldMethods){
			if (!Arrays.asList(newMethods).contains(method)){
				resultString += "Deleted method " + method.getName() + "\n";
			}
		}
		return resultString;
	}
	
	public static String ChangeMethod(Class newClass, Class oldClass){
		return "";
	}
	
	public static String AddField(Class newClass, Class oldClass){
		Field[] newFields = newClass.getDeclaredFields();
		Field[] oldFields = oldClass.getDeclaredFields();
		String resultString = "";
		for (Field fieldNew : newFields){
			boolean found = false;
			for (Field fieldOld: oldFields){
				if (fieldOld.getName().equals(fieldNew.getName())){
					found = true;
				}
			}
			if (!found){
				resultString += "Added field " + fieldNew.getName() + "\n";
			}
		}
		return resultString;
	}
	
	public static String DeleteField(Class newClass, Class oldClass){
		return "";
	}
	
	public static String ChangeFieldInit(Class newClass, Class oldClass){
		Field[] newFields = newClass.getFields();
		Field[] oldFields = oldClass.getFields();
		try{
		Constructor<?> ctor = newClass.getConstructor(String.class);
		Object newObject = ctor.newInstance(new Object[] {});
		Object oldObject = oldClass.newInstance();
		String resultString = "";
		List<Field> oldList = Arrays.asList(oldFields);
		for (Field field : newFields){
			if (oldList.contains(field)){
				Field oldField = oldList.get(oldList.indexOf(field));
				if (!oldField.get(oldObject).equals(field.get(newObject))){
					resultString += "Changed field initializer " + field.getName() + "\n";
				}
			}
		}
		return resultString;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
}
