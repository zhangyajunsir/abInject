package cn.com.agree.inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.prerequisite.IPrerequisite;

public class InjectLauncher implements IPrerequisite {
	private static final Logger	logger	= LoggerFactory.getLogger(InjectLauncher.class);

	@Override
	public IStatus start(IProgressMonitor monitor) {
		boolean isEnabled = Platform.getPreferencesService().getBoolean("cn.com.agree.inject", "EnabledInject", false, null);
		if (isEnabled) {
			logger.info("����ע�����Ԥ������ʼ...");
			try {
				InjectPlugin.getDefault().startInject();
			} catch (Throwable e) {
				logger.error("����ע�����Ԥ����ʧ��...", e);
				return Status.CANCEL_STATUS;
			}
			logger.info("����ע�����Ԥ��������...");
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus stop(IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}

}
