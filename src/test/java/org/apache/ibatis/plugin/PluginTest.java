/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.plugin;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Mybatis plugin 实现原理
 * <p>
 * 1、实现 Mybatis的Interceptor接口，并在实现类上加@Intercepts注解，标明要拦截的那个类，哪个方法(args指明参数)
 * 2、调用Interceptor的plugin方法时，Plugin.wrap(target, this)，查看该方法是否需要代理，如果需要代理，利用 JDK 动态代理返回代理对象，否者返回原对象
 * 3、代理的类实现了InvocationHandler，在方法调用的时候 利用 InvocationHandler 的 invoke方法调用了 mybatis 的插件建intercept方法
 */


public class PluginTest {

    @Test
    public void mapPluginShouldInterceptGet() {
        Map map = new HashMap();
        map.put("sssss", "sss");
        map = (Map) new AlwaysMapPlugin().plugin(map);
        System.out.println(map.get("sssss"));
        System.out.println(map.put("", ""));
        assertEquals("Always", map.get("Anything"));
    }

    @Test
    public void shouldNotInterceptToString() {
        Map map = new HashMap();
        map = (Map) new AlwaysMapPlugin().plugin(map);
        assertFalse("Always".equals(map.toString()));
    }

    @Intercepts({
            @Signature(type = Map.class, method = "get", args = {Object.class}),
            @Signature(type = Map.class, method = "put", args = {Object.class, Object.class})
    })
    public static class AlwaysMapPlugin implements Interceptor {
        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            System.out.println("mybatis 插件代理");
            return invocation.getMethod().invoke(invocation.getTarget(), invocation.getArgs());
        }

        @Override
        public Object plugin(Object target) {
            return Plugin.wrap(target, this);
        }

        @Override
        public void setProperties(Properties properties) {
        }
    }

}
