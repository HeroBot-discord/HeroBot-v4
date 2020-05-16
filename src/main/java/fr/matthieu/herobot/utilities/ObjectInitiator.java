package fr.matthieu.herobot.utilities;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;

public class ObjectInitiator<T> {

    private final Class<? extends T> theClass;

    public ObjectInitiator(Class<? extends T> aClass) {
        this.theClass = aClass;
    }

    public T buildObject(Object[] objects) throws InvalidClassException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (this.theClass.getConstructors().length == 0)
            throw new InvalidClassException("Cannot instantiate a class with no constructor.");
        int objectsSize = objects.length;
        for (Constructor<?> constructor : this.theClass.getConstructors()) {
            if (constructor.getParameterCount() > objectsSize) continue;
            Object[] parametersObject = new Object[constructor.getParameterCount()];
            int i = 0;
            for (Class<?> parameterType : constructor.getParameterTypes()) {
                Object parameter = null;
                for (Object object : objects) {
                    if (parameterType.getName().equals(object.getClass().getName())) {
                        parameter = object;
                    }
                }
                if (parameter == null) continue;
                parametersObject[i] = parameter;
                i++;
            }
            if (i != constructor.getParameterCount()) break;
            return (T) constructor.newInstance(parametersObject);
        }
        throw new InvalidParameterException("Can't find a suitable constructor.");
    }
}
