/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.prime.web.thumbnailator.util;

import java.util.function.Supplier;

/**
 *
 * Port some functionality of runtime assertion from springframeowrk.
 *
 */
public abstract class Assert
{

    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     *
     * <pre class="code">
     * Assert.isTrue( i &gt; 0, () -&gt; "The value '" + i + "' must be greater than zero" );
     * </pre>
     *
     * @param expression
     *            a boolean expression
     * @param messageSupplier
     *            a supplier for the exception message to use if the assertion
     *            fails
     * @throws IllegalArgumentException
     *             if {@code expression} is {@code false}
     */
    public static void isTrue( final boolean expression, final Supplier<String> messageSupplier )
    {
        if ( !expression )
        {
            throw new IllegalArgumentException( nullSafeGet( messageSupplier ) );
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     *
     * <pre class="code">
     * Assert.isTrue( i &gt; 0, "The value must be greater than zero" );
     * </pre>
     *
     * @param expression
     *            a boolean expression
     * @param message
     *            the exception message to use if the assertion fails
     * @throws IllegalArgumentException
     *             if {@code expression} is {@code false}
     */
    public static void isTrue( final boolean expression, final String message )
    {
        if ( !expression )
        {
            throw new IllegalArgumentException( message );
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     *
     * <pre class="code">
     * Assert.isTrue( i &gt; 0 );
     * </pre>
     *
     * @param expression
     *            a boolean expression
     * @throws IllegalArgumentException
     *             if {@code expression} is {@code false}
     */
    public static void isTrue( final boolean expression )
    {
        isTrue( expression, "[Assertion failed] - this expression must be true" );
    }

    /**
     * Assert that an object is {@code null}.
     *
     * <pre class="code">
     * Assert.isNull( value, () -&gt; "The value '" + value + "' must be null" );
     * </pre>
     *
     * @param object
     *            the object to check
     * @param messageSupplier
     *            a supplier for the exception message to use if the assertion
     *            fails
     * @throws IllegalArgumentException
     *             if the object is not {@code null}
     */
    public static void isNull( final Object object, final Supplier<String> messageSupplier )
    {
        if ( object != null )
        {
            throw new IllegalArgumentException( nullSafeGet( messageSupplier ) );
        }
    }

    /**
     * Assert that an object is {@code null}.
     *
     * <pre class="code">
     * Assert.isNull( value, "The value must be null" );
     * </pre>
     *
     * @param object
     *            the object to check
     * @param message
     *            the exception message to use if the assertion fails
     * @throws IllegalArgumentException
     *             if the object is not {@code null}
     */
    public static void isNull( final Object object, final String message )
    {
        if ( object != null )
        {
            throw new IllegalArgumentException( message );
        }
    }

    /**
     * Assert that an object is {@code null}.
     *
     * <pre class="code">
     * Assert.isNull( value );
     * </pre>
     *
     * @param object
     *            the object to check
     * @throws IllegalArgumentException
     *             if the object is not {@code null}
     */
    public static void isNull( final Object object )
    {
        isNull( object, "[Assertion failed] - the object argument must be null" );
    }

    /**
     * Assert that an object is not {@code null}.
     *
     * <pre class="code">
     * Assert.notNull( clazz, () -&gt; "The class '" + clazz.getName() + "' must not be null" );
     * </pre>
     *
     * @param object
     *            the object to check
     * @param messageSupplier
     *            a supplier for the exception message to use if the assertion
     *            fails
     * @throws IllegalArgumentException
     *             if the object is {@code null}
     */
    public static void notNull( final Object object, final Supplier<String> messageSupplier )
    {
        if ( object == null )
        {
            throw new IllegalArgumentException( nullSafeGet( messageSupplier ) );
        }
    }

    /**
     * Assert that an object is not {@code null}.
     *
     * <pre class="code">
     * Assert.notNull( clazz, "The class must not be null" );
     * </pre>
     *
     * @param object
     *            the object to check
     * @param message
     *            the exception message to use if the assertion fails
     * @throws IllegalArgumentException
     *             if the object is {@code null}
     */
    public static void notNull( final Object object, final String message )
    {
        if ( object == null )
        {
            throw new IllegalArgumentException( message );
        }
    }

    /**
     * Assert that an object is not {@code null}.
     *
     * <pre class="code">
     * Assert.notNull( clazz );
     * </pre>
     *
     * @param object
     *            the object to check
     * @throws IllegalArgumentException
     *             if the object is {@code null}
     */
    public static void notNull( final Object object )
    {
        notNull( object, "[Assertion failed] - this argument is required; it must not be null" );
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException} if
     * the expression evaluates to {@code false}.
     * <p>
     * Call {@link #isTrue} if you wish to throw an
     * {@code IllegalArgumentException} on an assertion failure.
     *
     * <pre class="code">
     * Assert.state( id == null, () -&gt; "ID for " + entity.getName() + " must not already be initialized" );
     * </pre>
     *
     * @param expression
     *            a boolean expression
     * @param messageSupplier
     *            a supplier for the exception message to use if the assertion
     *            fails
     * @throws IllegalStateException
     *             if {@code expression} is {@code false}
     */
    public static void state( final boolean expression, final Supplier<String> messageSupplier )
    {
        if ( !expression )
        {
            throw new IllegalStateException( nullSafeGet( messageSupplier ) );
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException} if
     * the expression evaluates to {@code false}.
     * <p>
     * Call {@link #isTrue} if you wish to throw an
     * {@code IllegalArgumentException} on an assertion failure.
     *
     * <pre class="code">
     * Assert.state( id == null, "The id property must not already be initialized" );
     * </pre>
     *
     * @param expression
     *            a boolean expression
     * @param message
     *            the exception message to use if the assertion fails
     * @throws IllegalStateException
     *             if {@code expression} is {@code false}
     */
    public static void state( final boolean expression, final String message )
    {
        if ( !expression )
        {
            throw new IllegalStateException( message );
        }
    }

    /**
     * Assert a boolean expression, throwing an {@link IllegalStateException} if
     * the expression evaluates to {@code false}.
     * <p>
     * Call {@link #isTrue} if you wish to throw an
     * {@code IllegalArgumentException} on an assertion failure.
     *
     * <pre class="code">
     * Assert.state( id == null );
     * </pre>
     *
     * @param expression
     *            a boolean expression
     * @throws IllegalStateException
     *             if {@code expression} is {@code false}
     */
    public static void state( final boolean expression )
    {
        state( expression, "[Assertion failed] - this state invariant must be true" );
    }

    private static String nullSafeGet( final Supplier<String> messageSupplier )
    {
        return ( messageSupplier != null ? messageSupplier.get() : null );
    }

}