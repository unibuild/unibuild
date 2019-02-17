package net.unibld.server.web.jsf.view;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import net.unibld.core.config.global.GlobalConfig;
import net.unibld.core.plugin.PluginRegistry;
import net.unibld.server.web.WebAppVersionInfo;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name="dashboardBean")
@ViewScoped
public class DashboardBean {
	private static final Logger LOGGER=LoggerFactory.getLogger(DashboardBean.class);
	private static final String DF_TIME="yyyy.MM.dd HH:mm:ss";
	
	private DashboardModel model;
	
	@ManagedProperty(value = "#{globalConfig}")
    private GlobalConfig globalConfig;
	
	@ManagedProperty(value = "#{pluginRegistry}")
    private PluginRegistry pluginRegistry;
    
    @PostConstruct
    public void init() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
         
        column1.addWidget("server");
         
        column2.addWidget("config");
       
        model.addColumn(column1);
        model.addColumn(column2);
        LOGGER.info("Dashboard inited");
    }
     
    public void handleReorder(DashboardReorderEvent event) {
        /*FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary("Reordered: " + event.getWidgetId());
        message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());
         
        addMessage(message);*/
    }
     
    public void handleClose(CloseEvent event) {
        //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed", "Closed panel id:'" + event.getComponent().getId() + "'");
         
        //addMessage(message);
    }
     
    public void handleToggle(ToggleEvent event) {
        /*FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled", "Status:" + event.getVisibility().name());
         
        addMessage(message);*/
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
     
    public DashboardModel getModel() {
        return model;
    }
	
	
	
	public String getServerTime() {
		return new SimpleDateFormat(DF_TIME).format(new Date());
	}

	public String getServerStartTime() {
		long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
		return new SimpleDateFormat(DF_TIME).format(new Date(jvmStartTime));
	}
	
	public String getServerUptime() {
		SimpleDateFormat durationFormat=new SimpleDateFormat("HH:mm:ss");
		
		long jvmUptime = ManagementFactory.getRuntimeMXBean().getUptime();
		
		//minus 1 hour hack
		jvmUptime-=(60*60*1000);
		return durationFormat.format(new Date(jvmUptime));
	}
	
	public String getIpAddress() {
		InetAddress ip;
		try {

			ip = InetAddress.getLocalHost();
			return ip.getHostAddress();

		} catch (UnknownHostException e) {

			LOGGER.error("Localhost is unknown", e);
			throw new IllegalStateException("Localhost is unknown", e);
		}

	}
	
	public String getVmVendor() {
		return ManagementFactory.getRuntimeMXBean().getVmVendor();
	}
	public String getVmVersion() {
		return ManagementFactory.getRuntimeMXBean().getVmVersion();
	}
	public String getVmName() {
		return ManagementFactory.getRuntimeMXBean().getVmName();
	}
	
	public MemoryUsage getHeapUsage() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
	}
	public MemoryUsage getNonHeapUsage() {
		return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
	}
	
	public double getSystemLoadAverage() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}
	
	public String getOsArch() {
		return ManagementFactory.getOperatingSystemMXBean().getArch();
	}
	public String getOsVersion() {
		return ManagementFactory.getOperatingSystemMXBean().getVersion();
	}
	public String getOsName() {
		return ManagementFactory.getOperatingSystemMXBean().getName();
	}
	public int getThreadCount() {
		return ManagementFactory.getThreadMXBean().getThreadCount();
	}

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}
	
	public Set<String> getPluginNames() {
		return pluginRegistry.getPluginNames();
	}

	public PluginRegistry getPluginRegistry() {
		return pluginRegistry;
	}

	public void setPluginRegistry(PluginRegistry pluginRegistry) {
		this.pluginRegistry = pluginRegistry;
	}
	

	public String getServerVersion() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		return WebAppVersionInfo.getWarVersionLabel(servletContext);
	}

 }
