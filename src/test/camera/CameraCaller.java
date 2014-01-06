package test.camera;

import android.widget.FrameLayout;
import test.Tester;

public interface CameraCaller extends Tester{
	public void stopByFail(int fail);
	public FrameLayout getPreviewFrameLayout();
}
