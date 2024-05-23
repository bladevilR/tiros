package org.jeecg.modules.shiro.authc.aop;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import cn.hutool.core.util.StrUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.WebUtil;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author Scott
 * @create 2019-02-01 15:56
 * @desc 鉴权请求URL访问权限拦截器
 */
@Slf4j
public class ResourceCheckFilter extends AccessControlFilter {

	public ResourceCheckFilter() {

	}

	public ResourceCheckFilter(List<String> pers) {
		this.allPermission.addAll(pers);
	}

	private List<String> allPermission = new ArrayList<>();

	private String errorUrl;

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	/**
	 * 表示是否允许访问 ，如果允许访问返回true，否则false；
	 * 
	 * @param servletRequest
	 * @param servletResponse
	 * @param o  表示写在拦截器中括号里面的字符串 mappedValue 就是 [urls] 配置中拦截器参数部分
	 * @return
	 * @throws Exception
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
		Subject subject = getSubject(servletRequest, servletResponse);
		String url = getPathWithinApplication(servletRequest);
		// log.info("当前用户正在访问的 url => " + url);
		// 如果这个资源在sys_permission中，则验证其是否具有权限，否则不验证
		String permission = this.isControlled(url);
		if (StrUtil.isNotBlank(permission)) {
			boolean result = subject.isPermitted(permission);
			if (!result) {
				log.warn("当前用户没有权限访问资源：{}", url);
				WebUtil.responseJsonResult(servletResponse, Result.error("你没有权限访问该接口"), CommonConstant.SC_JEECG_NO_AUTHZ);
			}
			return result;
		} else {
			return true;
		}
	}

	/**
	 * onAccessDenied：表示当访问拒绝时是否已经处理了； 如果返回 true 表示需要继续处理； 如果返回 false
	 * 表示该拦截器实例已经处理了，将直接返回即可。
	 * 
	 * @param servletRequest
	 * @param servletResponse
	 * @return
	 * @throws Exception
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
		// log.info("当 isAccessAllowed 返回 false 的时候，才会执行 method onAccessDenied ");

		/*HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.sendRedirect(request.getContextPath() + this.errorUrl);*/

		// 返回 false 表示已经处理，例如页面跳转啥的，表示不在走以下的拦截器了（如果还有配置的话）
		return false;
	}

	/** 判断该url是否在受控表中
	 * @return*/
	protected String isControlled(String url) {
		AtomicReference<String> pUrl = new AtomicReference<>("");
		this.allPermission.forEach(permission -> {
			if (StringUtils.isEmpty(permission)) {
				if (this.isMather(url, permission)) {
					pUrl.set(permission);
					return;
				}
			}
		});
		return pUrl.get();
	}

	protected boolean isMather(String targetUrl, String permissionUrl) {
		// 当前访问地址的前端与原地址（有权限）一样
		// System.out.println("检查：" + targetUrl + "           " + permissionUrl);
		if (targetUrl.startsWith(permissionUrl)) {
			// 当前是否一模一样
			if (targetUrl.equals(permissionUrl)) {
				return true;
			}
			// 源地址（有权限）的是当前访问地址的前缀
			return targetUrl.startsWith(permissionUrl.concat("/"));
		}
		return false;
	}
}