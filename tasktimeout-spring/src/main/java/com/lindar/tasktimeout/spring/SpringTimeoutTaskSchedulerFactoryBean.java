package com.lindar.tasktimeout.spring;

import com.lindar.tasktimeout.core.DefaultTimeoutManager;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.StringValueResolver;

public class SpringTimeoutTaskSchedulerFactoryBean  extends AbstractFactoryBean<TimeoutTaskScheduler> implements EmbeddedValueResolverAware  {

    private final TaskScheduler taskScheduler;

    private final long defaultTimeout;

    private StringValueResolver embeddedValueResolver;

    public SpringTimeoutTaskSchedulerFactoryBean(TaskScheduler taskScheduler, long defaultTimeout) {
        this.taskScheduler = taskScheduler;
        this.defaultTimeout = defaultTimeout;
    }

    @Override
    public Class<?> getObjectType() {
        return TimeoutTaskScheduler.class;
    }

    @Override
    protected TimeoutTaskScheduler createInstance() {
        return new TimeoutTaskScheduler(taskScheduler, new DefaultTimeoutManager(new SpringTimeoutConfigurationExtractor(defaultTimeout, embeddedValueResolver)));
    }

    @Override
    protected void destroyInstance(TimeoutTaskScheduler instance) throws Exception {
        instance.destroy();
    }


    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
}
