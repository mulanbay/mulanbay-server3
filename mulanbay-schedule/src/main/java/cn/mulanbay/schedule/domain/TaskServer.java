package cn.mulanbay.schedule.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 调度服务器
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
@Entity
@Table(name = "task_server")
@DynamicInsert
@DynamicUpdate
public class TaskServer implements java.io.Serializable {

	private static final long serialVersionUID = -6002198656881095798L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	/**
	 * 服务器节点
	 */
	@Column(name = "deploy_id", unique = true, nullable = false, length = 32)
	private String deployId;

	/**
	 * IP地址
	 */
	@Column(name = "ip_address", nullable = false, length = 32)
	private String ipAddress;

	/**
	 * 状态：在线、离线
	 */
	@Column(name = "status")
	private Boolean status;

	/**
	 * 是否支持分布式
	 */
	@Column(name = "distriable")
	private Boolean distriable;

	/**
	 * 当前正在运行的job数
	 */
	@Column(name = "cejc")
	private Integer cejc;

	/**
	 * 被调度的任务数
	 */
	@Column(name = "sjc")
	private Integer sjc;

	/**
	 * 启动时间
	 */
	@Column(name = "start_time")
	private Date startTime;

	/**
	 * 最后更新时间
	 */
	@Column(name = "modify_time")
	private Date modifyTime;

	/**
	 * 停止时间
	 */
	@Column(name = "shutdown_time")
	private Date shutdownTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Boolean getDistriable() {
		return distriable;
	}

	public void setDistriable(Boolean distriable) {
		this.distriable = distriable;
	}

	public Integer getCejc() {
		return cejc;
	}

	public void setCejc(Integer cejc) {
		this.cejc = cejc;
	}

	public Integer getSjc() {
		return sjc;
	}

	public void setSjc(Integer sjc) {
		this.sjc = sjc;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Date getShutdownTime() {
		return shutdownTime;
	}

	public void setShutdownTime(Date shutdownTime) {
		this.shutdownTime = shutdownTime;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaskServer) {
			TaskServer server = (TaskServer) other;
			return server.getId().equals(this.getId());
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}