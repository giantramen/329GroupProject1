package diff;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Stack;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Comparison {

	public static void main(String[] args) throws ClassNotFoundException, MalformedURLException, FileNotFoundException {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter full path to newest file:");
		String newFile = input.next();
		System.out.println("Enter full path to old file:");
		String oldFile = input.next();
		String javaDir = "C:\\Program Files (x86)\\Java\\";
		File javaFile = new File(javaDir);
		String[] dirContents = javaFile.list();
		boolean javaPathFound = false;
		for(String f : dirContents){
			if(new File(javaDir+f).isDirectory()){
				//requires jdk version 1.8.
				if(f.contains("jdk1.8")){
					javaDir += f;
					System.setProperty("java.home", javaDir);
					javaPathFound = true;
					break;
				}
			}
		}
		if(!javaPathFound){
			System.out.println("Error: Could not locate Java Developer Kit 1.8 in " + javaDir 
					+ "\nPlease install in that directory from and try again: \n" + 
					"http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jdk-8u60-windows-i586.exe");
			System.exit(0);
		}
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
			System.out.println("CM:\n" + ChangeMethod(newClass, oldClass, new File(newFile), new File(oldFile)));
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
				if(isSameMethod(methodNew, methodOld))
					found = true;
			}
			// ignores erroneous methods from inner classes.
			if(!found && !methodNew.getName().contains("access$")){
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
				if(isSameMethod(methodOld, methodNew))
					found = true;
			}
			// ignores erroneous methods from inner classes.
			if(!found && !methodOld.getName().contains("access$")){
				resultString += "Deleted Method " + methodOld.getName() + "\n";
			}
		}
		return resultString;
	}
	
	
	// Part 3
	public static String ChangeMethod(Class newClass, Class oldClass, File newFile, File oldFile) throws FileNotFoundException{
		Method[] newMethods = newClass.getDeclaredMethods();
		Method[] oldMethods = oldClass.getDeclaredMethods();
		String resultString = "";
		
		// clean out methods from internal private classes
		List<Method> newMeth = new ArrayList<Method>();
		List<Method> oldMeth = new ArrayList<Method>();
		for(Method method : newMethods)
			if(!method.getName().contains("access$"))
				newMeth.add(method);
		for(Method method : oldMethods)
			if(!method.getName().contains("access$"))
				oldMeth.add(method);
			
		for (Method methodNew : newMeth){
			boolean changed = false;
			for(Method methodOld : oldMeth){
				// if same method signature
				if(isSameMethod(methodNew, methodOld))
					// if same method signature but different body
					if(!isSameMethodContents(methodNew, methodOld, newFile, oldFile))
						changed = true;
			}
			if(changed){
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
		if(m1.getName().equals(m2.getName()) && m1.getModifiers() == m2.getModifiers() 
				&& Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes()) && m1.getReturnType().equals(m2.getReturnType()))
			return true;
		return false;
	}
	
	private static boolean isSameMethodContents(Method mNew, Method mOld, File newFile, File oldFile) throws FileNotFoundException{
		Scanner newScan = new Scanner(newFile);
		Scanner oldScan = new Scanner(oldFile);
		StringBuffer sbNew = new StringBuffer();
		StringBuffer sbOld = new StringBuffer();
		
		// skip to method body
		newScan.useDelimiter(mNew.getName());
		oldScan.useDelimiter(mOld.getName());
		newScan.next();
		oldScan.next();
		newScan.useDelimiter("\\{");
		oldScan.useDelimiter("\\{");
		
		Stack<String> bracketsNew = new Stack<String>();
		Stack<String> bracketsOld = new Stack<String>();
		newScan.useDelimiter("\\}");
		oldScan.useDelimiter("\\}");
		do {
			String sectionNew = newScan.next(); //skips to next "}"
			sbNew.append(sectionNew + "}");
			if(sectionNew.contains("{")){
				for(int i = 0; i < sectionNew.length(); i++){
					if(sectionNew.charAt(i) == '{')
						bracketsNew.push("{");
				}
			}
			bracketsNew.pop();
		}  while(!bracketsNew.isEmpty());
		do {
			String sectionOld = oldScan.next(); //skips to next "}"
			sbOld.append(sectionOld + "}");
			if(sectionOld.contains("{")){
				for(int i = 0; i < sectionOld.length(); i++){
					if(sectionOld.charAt(i) == '{')
						bracketsOld.push("{");
				}
			}
			bracketsOld.pop();
		} while(!bracketsOld.isEmpty());
		newScan.close();
		oldScan.close();
		// return true if method bodies are the same.
		if(sbNew.toString().equals(sbOld.toString()))
			return true;
		return false;
	}
}
