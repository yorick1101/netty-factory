package me.yorick.network.message;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;
import me.yorick.network.message.definition.Order;
import me.yorick.network.message.exception.NotInterfaceException;


public class BinaryImplementationFactory {

	private static NotInterfaceException NOT_INTERFACE_EXCEPTION = new NotInterfaceException();
	private static InvalidDefinitionException INVALID_DEFINITION_EXCEPTION = new InvalidDefinitionException();
	
	public static Class<?> generate(Class<?> clazz) throws Exception {
		if(!clazz.isInterface() && ! clazz.isAnnotationPresent(DefinitionAnnotation.class))
			throw NOT_INTERFACE_EXCEPTION;
		
		Map<String, Method[]> fieldToMethods = new HashMap<>();
		for(Method method : clazz.getMethods()) {
			if(method.isAnnotationPresent(ReadField.class)) {
				ReadField field = method.getAnnotation(ReadField.class);
				String fname = field.name();
				Method[] readWriteMethods = fieldToMethods.computeIfAbsent(fname, k -> new Method[2]);
				readWriteMethods[0] = method;
				
			}else if(method.isAnnotationPresent(WriteField.class)) {
				WriteField field = method.getAnnotation(WriteField.class);
				String fname = field.name();
				Method[] readWriteMethods = fieldToMethods.computeIfAbsent(fname, k -> new Method[2]);
				readWriteMethods[1] = method;
			} 
		}
		
		//check
		for(Entry<String, Method[]> entry: fieldToMethods.entrySet()) {
			Method[] methods = entry.getValue();
			if(methods[0]==null || methods[1]==null)
				throw INVALID_DEFINITION_EXCEPTION;
			
		}
		
		
		//create binary class
		ClassPool clazzPool = ClassPool.getDefault();
		ClassFile cf = new ClassFile(false, clazz.getName()+"Message", null);
		cf.setInterfaces(new String[] {clazz.getName()});
		cf.setAccessFlags(AccessFlag.PUBLIC);
		
		CtClass newClazz = ClassPool.getDefault().makeClass(cf);
		newClazz.addConstructor(CtNewConstructor.defaultConstructor(newClazz));
		CtClass bufferClazz = clazzPool.get("io.netty.buffer.ByteBuf");
		//CtClass bufferPoolClazz = clazzPool.get("io.netty.buffer.Unpooled");
		CtField bufferField = new CtField(bufferClazz, "buffer", newClazz);
		newClazz.addField(bufferField, CtField.Initializer.byExpr("io.netty.buffer.Unpooled.buffer();"));
		
		
		for(Entry<String, Method[]> entry: fieldToMethods.entrySet()) {
			
			Method[] methods = entry.getValue();
			//get
			Method oGetMethod = methods[0];
			CtClass returnType = clazzPool.get(oGetMethod.getReturnType().getName());
			CtMethod method = CtNewMethod.make(returnType, oGetMethod.getName(), null, null, "{System.out.println(\"get\");return null;}", newClazz);
			newClazz.addMethod(method);
			//set
			Method oSetMethod = methods[1];
			Parameter[] setParameters = oSetMethod.getParameters();
			CtClass[] setParameterClazzes = new CtClass[setParameters.length];
			int i =0;
			for(Parameter pClazz : setParameters) {
				setParameterClazzes[i] = clazzPool.get(pClazz.getType().getName());
				i++;
			}
			System.out.println("new method:"+oSetMethod.getName());
			CtMethod setMethod = CtNewMethod.make(CtClass.voidType, oSetMethod.getName(), setParameterClazzes, null, "{System.out.println(\"set:\"+$1);}", newClazz);
			
			newClazz.addMethod(setMethod);
			System.out.println(setMethod.toString());
			
		}
	
		return clazzPool.toClass(newClazz);
		
	}
			
	public static void main(String[] args) throws Exception {
		Class<?> generatedClass = BinaryImplementationFactory.generate(Order.class);
		Order order = (Order) generatedClass.newInstance();
		order.getExchange();
		order.setExchange("123");
	}
			
	
	
	public enum FieldType{
		Boolean("Z") ,Byte("B"), Character("C"), Double("D"), Float("F"), Integer("I"), Long("J"), Short("S"), Object("L");
		
		private final String character;
		
		FieldType(final String character){
			this.character = character;
		}
		
		public String getCharacter() {
			return character;
		}
	}
}
