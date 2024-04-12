package org.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to add to an object that is mapped to Elastic Search.
 * 
 * @author luc boutier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ESObject {
	boolean index() default true;

	boolean store() default false;

	boolean source() default true;

	boolean all() default true;

	IndexAnalyserDefinition[] analyzerDefinitions() default {};
}