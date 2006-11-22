/**
 *
 */
package org.hyperic.hq.events.server.mbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.common.shared.util.EjbModuleLifecycleListener;
import org.hyperic.hq.common.shared.util.EjbModuleLifecycle;
import org.hyperic.hq.common.shared.HQConstants;
import org.hyperic.hq.events.EscalationMediator;
import org.jboss.util.threadpool.ThreadPool;

import javax.management.ObjectName;
import javax.management.Notification;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import java.util.Date;

/**
 * MBean class that is called by the Scheduler to run escalation service
 *
 * @jmx:mbean name="hyperic.jmx:type=Service,name=EscalationService"
 */
public class EscalationScheduler
    implements EscalationSchedulerMBean, EjbModuleLifecycleListener,
               MBeanRegistration
{
    private static Log log = LogFactory.getLog(EscalationScheduler.class);

    private EjbModuleLifecycle haListener = null;
    private MBeanServer server = null;
    private boolean started = false;
    private ThreadPool threadPool;

    /**
     * this this instance
     *
     * @jmx:managed-attribute
     */
    public EscalationSchedulerMBean getInstance()
    {
        return this;
    }

    /**
     * get the thread pool object name
     *
     * @jmx:managed-attribute
     */
    public ObjectName getThreadPoolName()
    {
        return threadPoolName;
    }

    /**
     * set the thread pool object name
     *
     * @jmx:managed-attribute
     */
    public void setThreadPoolName(ObjectName threadPoolName)
    {
        this.threadPoolName = threadPoolName;
    }

    private ObjectName threadPoolName;

    /**
     * MBean entry point
     *
     * @jmx:managed-operation
     */
    public void hit(Notification notification, Date date,
                    long repetitions, ObjectName name)
    {
        if (!started) {
            if (log.isDebugEnabled()) {
                log.debug("HQ Services have not been started for " +
                          getClass().getName());
            }
            return;
        }
        log.info("Notification " + notification +
                 " EscalationService is called at: " + date +
                 ", remaining repetitions: " + repetitions +
                 ", test, name: " + name);

        threadPool.run(new Runnable() {
            public void run()
            {
                EscalationMediator.getInstance().processEscalation();
            }
        });
    }

    /**
     * run a Job in a threadPool
     *
     * @jmx:managed-operation
     */
    public void run(Runnable runnable)
    {
        threadPool.run(runnable);
    }

    /**
     * @jmx:managed-operation
     */
    public void init()
    {
    }

    /**
     * @jmx:managed-operation
     */
    public void start() throws Exception
    {
        haListener =
            new EjbModuleLifecycle(this.server, this,
                                   HQConstants.EJB_MODULE_PATTERN);
        haListener.start();
        threadPool = (ThreadPool)server.getAttribute(threadPoolName, "Instance");
    }

    /**
     * @jmx:managed-operation
     */
    public void stop()
    {
        threadPool = null;
        log.info("Stopping " + getClass().getName());
        haListener.stop();
    }

    /**
     * @jmx:managed-operation
     */
    public void destroy()
    {
    }

    public void ejbModuleStarted()
    {
        log.info("Starting " + getClass().getName());
        this.started = true;
    }

    public void ejbModuleStopped()
    {
    }

    public ObjectName preRegister(MBeanServer server,
                                  ObjectName name) throws Exception
    {
        this.server = server;
        return name;
    }

    public void postRegister(Boolean aBoolean)
    {
    }

    public void preDeregister() throws Exception
    {
    }

    public void postDeregister()
    {
    }
}
