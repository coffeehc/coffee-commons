package com.coffee.common.web.routing;

import javax.servlet.http.HttpServletRequest;

import com.coffee.common.base.core.interfaces.ILifecycleAware;
import com.coffee.common.web.routing.impl.RoutingDispatcherImpl;
import com.google.inject.ImplementedBy;

@ImplementedBy(RoutingDispatcherImpl.class)
public interface IRoutingDispatcher extends ILifecycleAware {

	public static final String NAMEED_SCAN_PACKAGES = "named_scan_webpackages";

	Object dispatch(HttpServletRequest request);

}
