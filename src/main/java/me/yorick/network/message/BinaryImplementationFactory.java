package me.yorick.network.message;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;
import me.yorick.network.message.exception.NotInterfaceException;


public class BinaryImplementationFactory {

	private static Logger logger = LoggerFactory.getLogger(BinaryImplementationFactory.class);
	private static final NotInterfaceException NOT_INTERFACE_EXCEPTION = new NotInterfaceException();
	private static final InvalidDefinitionException INVALID_DEFINITION_EXCEPTION = new InvalidDefinitionException();
	private static final String BUFFER_METHOD= "getBuffer()";
	private static final int STRING_LENGTH=64;
	private static final Map<Class<?>, String> byteBufWrite = getByteBufWriteBodyMap();
	private static final Map<Class<?>, String> byteBufRead = getByteBufReadBodyMap();
	
	public static Class<?> generate(Class<?> clazz) throws Exception {
		if(!clazz.isInterface() && ! clazz.isAnnotationPresent(DefinitionAnnotation.class))
			throw NOT_INTERFACE_EXCEPTION;
		DefinitionAnnotation definition = clazz.getAnnotation(DefinitionAnnotation.class);
		byte title = definition.title();
		
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
		int tlength = 1;
		for(Entry<String, Method[]> entry: fieldToMethods.entrySet()) {
			Method[] methods = entry.getValue();
			if(methods[0]==null || methods[1]==null)
				throw INVALID_DEFINITION_EXCEPTION;
			tlength += getLengthByType(methods[0].getReturnType());
		}
		
		
		//create binary class
		ClassPool clazzPool = ClassPool.getDefault();
		ClassFile cf = new ClassFile(false, clazz.getName()+"Message", null);
		cf.setSuperclass(BinaryMessage.class.getName());
		cf.setInterfaces(new String[] {clazz.getName()});
		cf.setAccessFlags(AccessFlag.PUBLIC);
		
		CtClass newClazz = ClassPool.getDefault().makeClass(cf);
		newClazz.addConstructor(CtNewConstructor.make("public "+newClazz.getSimpleName()+"() {super((byte)"+title+","+tlength+"); }", newClazz));
		
		int start = 0;
		for(Entry<String, Method[]> entry: fieldToMethods.entrySet()) {
			
			Method[] methods = entry.getValue();
			logger.debug("symbol:{}",entry.getKey());
			//get
			Method oGetMethod = methods[0];
			Class<?> type = oGetMethod.getReturnType();

			logger.debug("type:{}",type.getName());
			CtClass ctType = clazzPool.get(type.getName());
			
			String readBody = getReadBodyString(start, type);
			logger.debug("read:{}",readBody);
			CtMethod method = CtNewMethod.make(ctType, oGetMethod.getName(), null, null, readBody, newClazz);
			newClazz.addMethod(method);
			//set
			Method oSetMethod = methods[1];
			CtClass[] setParameterClazzes = {ctType};
			String writebody = getWriteBodyString(start, type);
			logger.debug("write:{}",writebody);
			CtMethod setMethod = CtNewMethod.make(CtClass.voidType, oSetMethod.getName(), setParameterClazzes, null, writebody, newClazz);
			newClazz.addMethod(setMethod);
			
			start= start+getLengthByType(type);
			
		}
	
		return clazzPool.toClass(newClazz);
		
	}
			
	private static int getLengthByType(Class<?> type) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		if(type.isPrimitive())
			return (int)type.getDeclaredField("BYTES").get(null);
		if(type.isEnum())
			return Byte.BYTES;
		if(type.isAssignableFrom(Boolean.class))
			return Byte.BYTES;
		if(type.isAssignableFrom(String.class)) {
			return STRING_LENGTH;
		}
		//TODO:: To supprt variant string length 
		throw new IllegalArgumentException(type.getName());
	}

	private static String getReadBodyString(int start, Class<?> type) throws Exception {
		String format = byteBufRead.get(type);
		if(format!=null) {
			return String.format(format, start);
		}
		if(type.isEnum()) {
			format = byteBufRead.get(Enum.class);
			return String.format(format, start, type.getName());
		}
		throw new Exception();
	}
	
	private static String getWriteBodyString(int start, Class<?> type) throws Exception {
		String format = byteBufWrite.get(type);
		if(format!=null) {
			return String.format(format, start);
		}
		if(type.isEnum()) {
			format = byteBufWrite.get(Enum.class);
			return String.format(format, start);
		}
		throw new Exception();
	}
	
	private static Map<Class<?>, String> getByteBufWriteBodyMap() {
		Map<Class<?>, String> mapping = new HashMap<>();
		mapping.put(Double.class, "{"+BUFFER_METHOD+".setDouble(%d, $1);}");
		mapping.put(Float.class, "{"+BUFFER_METHOD+".setFloat(%d, $1);}");
		mapping.put(Integer.class, "{"+BUFFER_METHOD+".setInt(%d, $1);}");
		mapping.put(Short.class, "{"+BUFFER_METHOD+".setShort(%d, $1);}");
		mapping.put(Boolean.class, "{"+BUFFER_METHOD+".setBoolean(%d, $1);}");
		mapping.put(Byte.class, "{"+BUFFER_METHOD+".setByte(%d, $1);}");
		mapping.put(Enum.class, "{"+BUFFER_METHOD+".setByte(%d, (byte)$1.ordinal());}");
		mapping.put(String.class, "{byte[] bs = $1.getBytes();"+BUFFER_METHOD+".setBytes(%d, bs , 0, (bs.length>"+STRING_LENGTH+")?"+STRING_LENGTH+":bs.length);}");
		
		return mapping;
	}
	
	private static Map<Class<?>, String> getByteBufReadBodyMap() {
		Map<Class<?>, String> mapping = new HashMap<>();
		mapping.put(Double.class, "{return "+BUFFER_METHOD+".getDouble(%d);}");
		mapping.put(Float.class, "{return "+BUFFER_METHOD+".getFloat(%d);}");
		mapping.put(Integer.class, "{return "+BUFFER_METHOD+".getInt(%d);}");
		mapping.put(Short.class, "{return "+BUFFER_METHOD+".getShort(%d);}");
		mapping.put(Boolean.class,"{return "+ BUFFER_METHOD+".getBoolean(%d);}");
		mapping.put(Byte.class, "{return "+BUFFER_METHOD+".getByte(%d);}");
		mapping.put(Enum.class, "{int o=(int)"+BUFFER_METHOD+".getByte(%d);return %s.values()[o];}");
		mapping.put(String.class, "{byte[] bs = new byte["+STRING_LENGTH+"];"+BUFFER_METHOD+".getBytes(%d, bs); return new String(bs).trim();}");
		return mapping;
		
	}
	
}
