package com.dozycoffee.infrastructure;

import com.dozycoffee.infrastructure.web.ServletConfig;
import jakarta.servlet.Filter;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Nullable
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { RootConfig.class };
    }

    @Nullable
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { ServletConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/api/v1/*" };
    }

    @Override
    protected Filter[] getServletFilters() {
        DelegatingFilterProxy sessionPrincipalFilter = new DelegatingFilterProxy("sessionPrincipalFilter");
        sessionPrincipalFilter.setTargetFilterLifecycle(true);
        return new Filter[] { sessionPrincipalFilter };
    }
}
