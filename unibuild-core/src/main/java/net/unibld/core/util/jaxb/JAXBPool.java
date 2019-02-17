package net.unibld.core.util.jaxb;

import java.util.Hashtable;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;

/**
 * A singleton class that pools JAXB marshallers and unmarshallers in commons-pool
 * {@link ObjectPool}s hashed by JAXB context class
 * @author andor
 *
 */
public class JAXBPool {
	private static JAXBPool inst;
	
	/**
	 * @return The {@link JAXBPool} singleton
	 */
	public static JAXBPool getInstance() {
		if (inst==null) {
			inst=new JAXBPool();
		}
		return inst;
	}
	
	private Hashtable<Class<?>, ObjectPool> unmarshallerPools=new Hashtable<Class<?>, ObjectPool>();
	private Hashtable<Class<?>, ObjectPool> marshallerPools=new Hashtable<Class<?>, ObjectPool>();
    

	/**
	 * Borrows an unmarshaller for the specified context class from the related
	 * {@link ObjectPool}.
	 * @param contextClass JAXB context class
	 * @return Borrowed {@link Unmarshaller}
	 * @throws Exception
	 */
	public Unmarshaller borrowUnmarshaller(Class<?> contextClass) throws Exception {
		ObjectPool pool = unmarshallerPools.get(contextClass);
		if (pool==null) {
			pool=createUnmarshallerPool(contextClass);
		}
		
		return (Unmarshaller) pool.borrowObject();
		
	}
	/**
	 * Borrows an marshaller for the specified context class from the related
	 * {@link ObjectPool}.
	 * @param contextClass JAXB context class
	 * @return Borrowed {@link Marshaller}
	 * @throws Exception
	 */
	public Marshaller borrowMarshaller(Class<?> contextClass) throws Exception {
		ObjectPool pool = marshallerPools.get(contextClass);
		if (pool==null) {
			pool=createMarshallerPool(contextClass);
		}
		
		return (Marshaller) pool.borrowObject();
		
	}
	
	private ObjectPool createUnmarshallerPool(Class<?> contextClass) {
		StackObjectPool pool = new StackObjectPool(new JAXBUnmarshallerFactory(contextClass));
		unmarshallerPools.put(contextClass, pool);
		return pool;
	}
	
	private ObjectPool createMarshallerPool(Class<?> contextClass) {
		StackObjectPool pool = new StackObjectPool(new JAXBMarshallerFactory(contextClass));
		marshallerPools.put(contextClass, pool);
		return pool;
	}
	/**
	 * Returns an unmarshaller with the specified context class to the related
	 * {@link ObjectPool}.
	 * @param contextClass JAXB context class
	 * @param um Borrowed {@link Unmarshaller} to return
	 * @throws Exception
	 */
	public void returnUnmarshaller(Class<?> contextClass, Unmarshaller um) throws Exception {
		if (um==null) {
			return;
		}
		
		ObjectPool pool = unmarshallerPools.get(contextClass);
		if (pool==null) {
			pool=createUnmarshallerPool(contextClass);
		}
		
		pool.returnObject(um);
	}
	/**
	 * Returns an marshaller with the specified context class to the related
	 * {@link ObjectPool}.
	 * @param contextClass JAXB context class
	 * @param um Borrowed {@link Marshaller} to return
	 * @throws Exception
	 */
	public void returnMarshaller(Class<?> contextClass, Marshaller um) throws Exception {
		if (um==null) {
			return;
		}
		
		ObjectPool pool = marshallerPools.get(contextClass);
		if (pool==null) {
			pool=createMarshallerPool(contextClass);
		}
		
		pool.returnObject(um);
	}
}
