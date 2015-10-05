package diff;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Comparison {

	
	
	public static void main(String[] args) throws ClassNotFoundException, MalformedURLException {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter full path to newest file");
		//String newFile = input.next();
		String newFile = "C:\\Users\\Michael Snook\\Desktop\\Modified\\ReboundPanel.java";
		System.out.println("Enter full path to old file");
		//String oldFile = input.next();	//TODO: remove hardcoded paths to files
		String oldFile = "C:\\Users\\Michael Snook\\Desktop\\ReboundPanel.java";
		System.setProperty("java.home", "C:\\Program Files (x86)\\Java\\jdk1.7.0_55");  //TODO: add scanner for jdk
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int firstCompilationResult = compiler.run(System.in, System.out, System.err, newFile);
		if(firstCompilationResult == 0){
			System.out.println("First Compilation is successful");
		}else{
			System.out.println("Compilation Failed");
		}
		int secondCompilationResult =	compiler.run(null, null, null, oldFile);
		if(secondCompilationResult == 0){
			System.out.println("Second Compilation is successful");
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
			System.out.println("AM:\n" + AddMethod(newClass, oldClass));
			System.out.println("DM:\n" + DeleteMethod(newClass, oldClass));
			System.out.println("CM:\n" + ChangeMethod(newClass, oldClass));
			System.out.println("AF:\n" + AddField(newClass, oldClass));
			System.out.println("DF:\n" + DeleteField(newClass, oldClass));
			System.out.println("CFI:\n" + ChangeFieldInit(newClass, oldClass));
		}
	}
	
	// Part 1
	public static String AddMethod(Class newClass, Class oldClass){
		Method[] newMethods = newClass.getDeclaredMethods();
		Method[] oldMethods = oldClass.getDeclaredMethods();
		String resultString = "";
		for (Method methodNew : newMethods){
			boolean found = false;
			for(Method methodOld : oldMethods){
				if(isSameMethod(methodNew, methodOld)/*methodNew.equals(methodOld)*/)
					found = true;
			}
			if(!found /*&& !methodNew.getName().contains("access$")*/){
				resultString += "Added Method " + methodNew.getName() + "\n";
			}
		}
		return resultString;
	}
	
	
	// Part 2
	public static String DeleteMethod(Class newClass, Class oldClass){
		Method[] newMethods = newClass.getDeclaredMethods();
		Method[] oldMethods = oldClass.getDeclaredMethods();
		String resultString = "";
		for (Method methodOld : oldMethods){
			boolean found = false;
			for(Method methodNew : newMethods){
				if(isSameMethod(methodOld, methodNew)/*methodNew.equals(methodOld)*/)
					found = true;
			}
			// ignores erroneous methods from inner classes.
			if(!found && !methodOld.getName().contains("access$")){
				resultString += "Deleted Method " + methodOld.getName() + "\n";
			}
		}
		return resultString;
	}
	
	
	//Part 3
	public static String ChangeMethod(Class newClass, Class oldClass){
		//TODO
		Method[] newMethods = newClass.getDeclaredMethods();
		Method[] oldMethods = oldClass.getDeclaredMethods();
		List<Method> newMeth = Arrays.asList(newMethods);
		List<Method> oldMeth = Arrays.asList(oldMethods);
		String resultString = "";
		
		//Delete access$ methods from inner classes
		/*for(int i = 0; i < newMeth.size(); i = i){
			if(newMeth.get(i).getName().contains("access$"))
				newMeth.remove(i);
			else
				i++;
		}
		for(int i = 0; i < oldMeth.size(); i = i){
			if(oldMeth.get(i).getName().contains("access$"))
				oldMeth.remove(i);
			else
				i++;
		}*/		
		for (Method methodNew : newMeth){
			boolean found = false;
			for(Method methodOld : oldMeth){
				if(isSameMethod(methodNew, methodOld))
					// if method has been reordered
					if(newMeth.indexOf(methodNew) != oldMeth.indexOf(methodOld))
						found = true;
			}
			if(!found){
				resultString += "Changed Method " + methodNew.getName() + "\n";
			}
		}
		return resultString;
	}
	
	
	// Part 4
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
	
	// Part 5
	public static String DeleteField(Class newClass, Class oldClass){
		Field[] newFields = newClass.getDeclaredFields();
		Field[] oldFields = oldClass.getDeclaredFields();
		String resultString = "";
		for (Field fieldOld : oldFields){
			boolean found = false;
			for (Field fieldNew: newFields){
				if (fieldNew.getName().equals(fieldOld.getName())){
					found = true;
				}
			}
			if (!found){
				resultString += "Deleted field " + fieldOld.getName() + "\n";
			}
		}
		return resultString;
	}
	
	
	// Part 6
	public static String ChangeFieldInit(Class newClass, Class oldClass){
		Field[] newFields = newClass.getDeclaredFields();
		Field[] oldFields = oldClass.getDeclaredFields();
		try{
		Constructor<?> ctor = newClass.getConstructor();
		Object newObject = ctor.newInstance();
		ctor = oldClass.getConstructor();
		Object oldObject = ctor.newInstance();
		String resultString = "";
		List<Field> oldList = Arrays.asList(oldFields);
		for (Field field : newFields){
			if (oldList.stream().filter(o -> o.getName().equals(field.getName())).findFirst().isPresent()){
				Field oldField = oldList.stream().filter(o -> o.getName().equals(field.getName())).findFirst().get();
				if (oldField.getModifiers() != field.getModifiers()){
					resultString += "Changed field accessability " + field.getName() + "\n";
				}
				if (!oldField.isAccessible()){
					oldField.setAccessible(true);
				}
				if (!field.isAccessible()){
					field.setAccessible(true);
				}
				if (oldField.get(oldObject) == null && field.get(newObject) != null){
					resultString += "Added field initializer " + field.getName() + "\n";
				}
				else if (oldField.get(oldObject) != null && field.get(newObject) == null){
					resultString += "Deleted field initializer " + field.getName() + "\n";
				}
				else if (!oldField.get(oldObject).equals(field.get(newObject))){
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
	
	// Helper function to check method equality
	private static boolean isSameMethod(Method m1, Method m2){
//		System.out.println(m1.getName());
//		System.out.println(m2.getName());
//		System.out.println(m1.getModifiers());
//		System.out.println(m2.getModifiers());
//		Class[] c1 = m1.getParameterTypes();
//		Class[] c2 = m2.getParameterTypes();
//		System.out.println(m1.getParameterTypes().toString());
//		System.out.println(m2.getParameterTypes().toString());
//		System.out.println(m1.getReturnType());
//		System.out.println(m2.getReturnType());
		if(m1.getName().equals(m2.getName()) && m1.getModifiers() == m2.getModifiers() 
				&& Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes()) && m1.getReturnType().equals(m2.getReturnType()))
			return true;
		return false;
	}
}
