/*
 * @(#)Methods.java  1.0.1 2006-06-25
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * ALL rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */

package com.jakeapp.gui.swing.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Methods contains convenience methods for method invocations using
 * java.lang.reflect.
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2006-06-25 Fixed comments.
 *          <br>1.0 September 24, 2005 Created.
 */
public class Methods {
	/**
	 * Prevent instance creation.
	 */
	private Methods() {
	}

	/**
	 * Invokes the specified accessible parameterless method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @return NoSuchMethodException if the method does not exist or is not
	 *         accessible.
	 * @throws NoSuchMethodException
	 */
	public static Object invoke(Object obj, String methodName)
			  throws NoSuchMethodException {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[0]);
			Object result = method.invoke(obj);
			return result;
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified accessible method with a string parameter if it exists.
	 *
	 * @param obj				 The object on which to invoke the method.
	 * @param methodName		The name of the method.
	 * @param stringParameter The String parameter
	 * @return NoSuchMethodException if the method does not exist or is not accessible.
	 * @throws NoSuchMethodException
	 */
	public static Object invoke(Object obj, String methodName, String stringParameter)
			  throws NoSuchMethodException {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[]{String.class});
			Object result = method.invoke(obj, stringParameter);
			return result;
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified accessible parameterless method if it exists.
	 *
	 * @param clazz		The class on which to invoke the method.
	 * @param methodName The name of the method.
	 * @return NoSuchMethodException if the method does not exist or is not accessible.
	 * @throws NoSuchMethodException
	 */
	public static Object invokeStatic(Class clazz, String methodName)
			  throws NoSuchMethodException {
		try {
			Method method = clazz.getMethod(methodName, new Class[0]);
			Object result = method.invoke(null);
			return result;
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified accessible parameterless method if it exists.
	 *
	 * @param clazz		The class on which to invoke the method.
	 * @param methodName The name of the method.
	 * @return NoSuchMethodException if the method does not exist or is not accessible.
	 * @throws NoSuchMethodException
	 */
	public static Object invokeStatic(String clazz, String methodName)
			  throws NoSuchMethodException {
		try {
			return invokeStatic(Class.forName(clazz), methodName);
		} catch (ClassNotFoundException e) {
			throw new NoSuchMethodException("class " + clazz + " not found");
		}
	}

	/**
	 * Invokes the specified parameterless method if it exists.
	 *
	 * @param clazz		The class on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param types		The parameter types.
	 * @param values	  The parameter values.
	 * @return NoSuchMethodException if the method does not exist or is not accessible.
	 * @throws NoSuchMethodException
	 */
	public static Object invokeStatic(Class clazz, String methodName, Class[] types, Object[] values)
			  throws NoSuchMethodException {
		try {
			Method method = clazz.getMethod(methodName, types);
			Object result = method.invoke(null, values);
			return result;
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified parameterless method if it exists.
	 *
	 * @param clazz		The class on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param types		The parameter types.
	 * @param values	  The parameter values.
	 * @return NoSuchMethodException if the method does not exist or is not accessible.
	 * @throws NoSuchMethodException
	 */
	public static Object invokeStatic(String clazz, String methodName,
												 Class[] types, Object[] values)
			  throws NoSuchMethodException {
		try {
			return invokeStatic(Class.forName(clazz), methodName, types, values);
		} catch (ClassNotFoundException e) {
			throw new NoSuchMethodException("class " + clazz + " not found");
		}
	}

	/**
	 * Invokes the specified parameterless method if it exists.
	 *
	 * @param clazz		  The class on which to invoke the method.
	 * @param methodName	The name of the method.
	 * @param types		  The parameter types.
	 * @param values		 The parameter values.
	 * @param defaultValue The default value.
	 * @return The return value of the method or the default value if the method
	 *         does not exist or is not accessible.
	 */
	public static Object invokeStatic(String clazz, String methodName,
												 Class[] types, Object[] values, Object defaultValue) {
		try {
			return invokeStatic(Class.forName(clazz), methodName, types, values);
		} catch (ClassNotFoundException e) {
			return defaultValue;
		} catch (NoSuchMethodException e) {
			return defaultValue;
		}
	}

	/**
	 * Invokes the specified getter method if it exists.
	 *
	 * @param obj			 The object on which to invoke the method.
	 * @param methodName	The name of the method.
	 * @param defaultValue This value is returned, if the method does not exist.
	 * @return The value returned by the getter method or the default value.
	 */
	public static int invokeGetter(Object obj, String methodName, int defaultValue) {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[0]);
			Object result = method.invoke(obj);
			return (Integer) result;
		} catch (NoSuchMethodException e) {
			return defaultValue;
		} catch (IllegalAccessException e) {
			return defaultValue;
		} catch (InvocationTargetException e) {
			return defaultValue;
		}
	}

	/**
	 * Invokes the specified getter method if it exists.
	 *
	 * @param obj			 The object on which to invoke the method.
	 * @param methodName	The name of the method.
	 * @param defaultValue This value is returned, if the method does not exist.
	 * @return The value returned by the getter method or the default value.
	 */
	public static long invokeGetter(Object obj, String methodName, long defaultValue) {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[0]);
			Object result = method.invoke(obj);
			return (Long) result;
		} catch (NoSuchMethodException e) {
			return defaultValue;
		} catch (IllegalAccessException e) {
			return defaultValue;
		} catch (InvocationTargetException e) {
			return defaultValue;
		}
	}

	/**
	 * Invokes the specified getter method if it exists.
	 *
	 * @param obj			 The object on which to invoke the method.
	 * @param methodName	The name of the method.
	 * @param defaultValue This value is returned, if the method does not exist.
	 * @return The value returned by the getter method or the default value.
	 */
	public static boolean invokeGetter(Object obj, String methodName, boolean defaultValue) {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[0]);
			Object result = method.invoke(obj);
			return (Boolean) result;
		} catch (NoSuchMethodException e) {
			return defaultValue;
		} catch (IllegalAccessException e) {
			return defaultValue;
		} catch (InvocationTargetException e) {
			return defaultValue;
		}
	}

	/**
	 * Invokes the specified getter method if it exists.
	 *
	 * @param obj			 The object on which to invoke the method.
	 * @param methodName	The name of the method.
	 * @param defaultValue This value is returned, if the method does not exist.
	 * @return The value returned by the getter method or the default value.
	 */
	public static Object invokeGetter(Object obj, String methodName, Object defaultValue) {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[0]);
			Object result = method.invoke(obj);
			return result;
		} catch (NoSuchMethodException e) {
			return defaultValue;
		} catch (IllegalAccessException e) {
			return defaultValue;
		} catch (InvocationTargetException e) {
			return defaultValue;
		}
	}

	/**
	 * Invokes the specified getter method if it exists.
	 *
	 * @param clazz		  The object on which to invoke the method.
	 * @param methodName	The name of the method.
	 * @param defaultValue This value is returned, if the method does not exist.
	 * @return The value returned by the getter method or the default value.
	 */
	public static boolean invokeStaticGetter(Class clazz, String methodName, boolean defaultValue) {
		try {
			Method method = clazz.getMethod(methodName, new Class[0]);
			Object result = method.invoke(null);
			return (Boolean) result;
		} catch (NoSuchMethodException e) {
			return defaultValue;
		} catch (IllegalAccessException e) {
			return defaultValue;
		} catch (InvocationTargetException e) {
			return defaultValue;
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param newValue
	 * @throws NoSuchMethodException
	 * @return
	 */
	public static Object invoke(Object obj, String methodName, boolean newValue)
			  throws NoSuchMethodException {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[]{Boolean.TYPE});
			return method.invoke(obj, newValue);
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param newValue
	 * @throws NoSuchMethodException
	 * @return
	 */
	public static Object invoke(Object obj, String methodName, int newValue)
			  throws NoSuchMethodException {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[]{Integer.TYPE});
			return method.invoke(obj, newValue);
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param newValue
	 * @throws NoSuchMethodException
	 * @return
	 */
	public static Object invoke(Object obj, String methodName, float newValue)
			  throws NoSuchMethodException {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[]{Float.TYPE});
			return method.invoke(obj, newValue);
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param clazz
	 * @param newValue
	 * @throws NoSuchMethodException
	 * @return
	 */
	public static Object invoke(Object obj, String methodName, Class clazz, Object newValue)
			  throws NoSuchMethodException {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[]{clazz});
			return method.invoke(obj, newValue);
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param clazz
	 * @param newValue
	 * @throws NoSuchMethodException
	 * @return
	 */
	public static Object invoke(Object obj, String methodName, Class[] clazz, Object... newValue)
			  throws NoSuchMethodException {
		try {
			Method method = obj.getClass().getMethod(methodName, clazz);
			return method.invoke(obj, newValue);
		} catch (IllegalAccessException e) {
			throw new NoSuchMethodException(methodName + " is not accessible");
		} catch (InvocationTargetException e) {
			// The method is not supposed to throw exceptions
			InternalError error = new InternalError(e.getMessage());
			error.initCause((e.getCause() != null) ? e.getCause() : e);
			throw error;
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 */
	public static void invokeIfExists(Object obj, String methodName) {
		try {
			invoke(obj, methodName);
		} catch (NoSuchMethodException e) {
			// ignore
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param newValue
	 */
	public static void invokeIfExists(Object obj, String methodName, float newValue) {
		try {
			invoke(obj, methodName, newValue);
		} catch (NoSuchMethodException e) {
			// ignore
		}
	}

	/**
	 * Invokes the specified method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param newValue
	 */
	public static void invokeIfExists(Object obj, String methodName, boolean newValue) {
		try {
			invoke(obj, methodName, newValue);
		} catch (NoSuchMethodException e) {
			// ignore
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param clazz
	 * @param newValue
	 */
	public static void invokeIfExists(Object obj, String methodName, Class clazz, Object newValue) {
		try {
			invoke(obj, methodName, clazz, newValue);
		} catch (NoSuchMethodException e) {
			// ignore
		}
	}

	/**
	 * Invokes the specified setter method if it exists.
	 *
	 * @param obj		  The object on which to invoke the method.
	 * @param methodName The name of the method.
	 * @param enumClassName
	 * @param enumValueName
	 */
	public static void invokeIfExistsWithEnum(Object obj, String methodName, String enumClassName, String enumValueName) {
		try {
			Class enumClass = Class.forName(enumClassName);
			Object enumValue = invokeStatic("java.lang.Enum", "valueOf", new Class[]{Class.class, String.class},
					  new Object[]{enumClass, enumValueName}
			);
			invoke(obj, methodName, enumClass, enumValue);
		} catch (ClassNotFoundException e) {
			// ignore
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// ignore
			e.printStackTrace();
		}
	}
}