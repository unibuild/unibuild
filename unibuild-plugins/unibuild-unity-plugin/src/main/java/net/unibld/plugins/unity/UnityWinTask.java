package net.unibld.plugins.unity;


/**
 * An abstract task that executes a Unity command on Windows
 * @author andor
 *
 */
public abstract class UnityWinTask extends UnityTask {
	
	//private boolean forceD3d9=true;
	private boolean forceD3d11=false;
	private boolean forceOpenGl=false;
	/**
	 * @return True if DirectX 11 rendering is forced (the default is DirectX 9)
	 */
	public boolean isForceD3d11() {
		return forceD3d11;
	}
	/**
	 * @param forceD3d11 True if DirectX 11 rendering is forced (the default is DirectX 9)
	 */
	public void setForceD3d11(boolean forceD3d11) {
		this.forceD3d11 = forceD3d11;
	}
	/**
	 * @return True if OpenGL rendering is forced (the default is DirectX 9)
	 */
	public boolean isForceOpenGl() {
		return forceOpenGl;
	}
	/**
	 * @param forceOpenGl True if OpenGL rendering is forced (the default is DirectX 9)
	 */
	public void setForceOpenGl(boolean forceOpenGl) {
		this.forceOpenGl = forceOpenGl;
	}
		
}
