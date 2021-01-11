package com.blackrook.gloop.glfw;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

/**
 * A GLFW monitor information instance.
 * @author Matthew Tropiano
 */
public class GLFWMonitor extends GLFWHandle
{
	private static GLFWMonitor[] ALL = null;
	
	/** The memory address. */
	private long handle;

	/** The monitor name. */
	private String name;
	/** Width in millimeters. */
	private int widthMM;
	/** Height in millimeters. */
	private int heightMM;
	/** Content scalar, X. */
	private float contentScaleX;
	/** Content scalar, Y. */
	private float contentScaleY;
	/** Screen coordinates position X. */
	private int positionX;
	/** Screen coordinates position X. */
	private int positionY;
	/** Desktop work area screen coordinates X. */
	private int workAreaX;
	/** Desktop work area screen coordinates Y. */
	private int workAreaY;
	/** Desktop work area screen coordinates width. */
	private int workAreaWidth;
	/** Desktop work area screen coordinates height. */
	private int workAreaHeight;
	
	/**
	 * Gets all available monitor devices.
	 * This must only be called from the main thread on the first call.
	 * @return an array of monitors.
	 */
	public static GLFWMonitor[] getAll()
	{
		if (ALL == null)
		{
			GLFWInit.init(); // init GLFW if not already (only happens once).
			PointerBuffer pb = GLFW.glfwGetMonitors();
			GLFWMonitor[] out = new GLFWMonitor[pb.capacity()];
			for (int i = 0; i < pb.capacity(); i++)
				out[i] = new GLFWMonitor(pb.get(i));
			ALL = out;
		}
		return ALL;
	}
	
	/**
	 * Gets the primary monitor.
	 * This must only be called from the main thread on the first call.
	 * @return the primary monitor.
	 */
	public static GLFWMonitor getPrimaryMonitor()
	{
		return getAll()[0];
	}
	
	/**
	 * Creates a new GLFW monitor and gathers its information to query.
	 * @param handle the memory handle.
	 */
	private GLFWMonitor(long handle) 
	{
		this.handle = handle;
		this.name = GLFW.glfwGetMonitorName(handle);
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf = stack.mallocInt(1);
			IntBuffer buf2 = stack.mallocInt(1);
			IntBuffer buf3 = stack.mallocInt(1);
			IntBuffer buf4 = stack.mallocInt(1);
			FloatBuffer fbuf = stack.mallocFloat(1);
			FloatBuffer fbuf2 = stack.mallocFloat(1);
			GLFW.glfwGetMonitorPhysicalSize(handle, buf, buf2);
			widthMM = buf.get();
			heightMM = buf2.get();
			GLFW.glfwGetMonitorContentScale(handle, fbuf, fbuf2);
			contentScaleX = fbuf.get();
			contentScaleY = fbuf2.get();
			GLFW.glfwGetMonitorPos(handle, buf, buf2);
			positionX = buf.get();
			positionY = buf2.get();
			GLFW.glfwGetMonitorWorkarea(handle, buf, buf2, buf3, buf4);
			workAreaX = buf.get();
			workAreaY = buf2.get();
			workAreaWidth = buf3.get();
			workAreaHeight = buf4.get();
		}
	}
		
	@Override
	public long getHandle()
	{
		return handle;
	}

	@Override
	public boolean isCreated()
	{
		return true;
	}

	@Override
	public void destroy()
	{
		throw new UnsupportedOperationException("Monitors cannot be destroyed.");
	}
	
	// TODO: Finish getters.

}
