package me.nulldoubt.testing.bgfx;

import org.lwjgl.bgfx.*;
import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TestingBGFX {
	
	public static void main(final String[] args) {
		
		final int width = 800;
		final int height = 450;
		
		GLFWErrorCallback.createThrow().set();
		if (!glfwInit())
			throw new RuntimeException("Unable to initialize GLFW");
		
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
		final long window = glfwCreateWindow(width, height, "Testing BGFX", 0, 0);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(window, (_, key, _, action, _) -> {
			if (action != GLFW_RELEASE)
				return;
			if (key == GLFW_KEY_ESCAPE)
				glfwSetWindowShouldClose(window, true);
		});
		
		try (MemoryStack stack = stackPush()) {
			final BGFXInit init = BGFXInit.malloc(stack);
			bgfx_init_ctor(init);
			init.resolution((resolution) -> resolution.width(width).height(height).reset(BGFX_RESET_VSYNC));
			init.platformData().nwh(GLFWNativeWin32.glfwGetWin32Window(window));
			//init.type(BGFX_RENDERER_TYPE_DIRECT3D12);
			init.type(BGFX_RENDERER_TYPE_OPENGL);
			
			if (!bgfx_init(init))
				throw new RuntimeException("Failed to initialize BGFX");
		}
		
		System.out.println("BGFX Renderer: " + bgfx_get_renderer_name(bgfx_get_renderer_type()));
		
		bgfx_set_debug(BGFX_DEBUG_TEXT);
		bgfx_set_view_clear(0, BGFX_CLEAR_COLOR | BGFX_CLEAR_DEPTH, 0x303030ff, 1f, 0);
		
		final ByteBuffer logo = Logo.createLogo();
		
		while (!glfwWindowShouldClose(window)) {
			glfwPollEvents();
			
			bgfx_set_view_rect(0, 0, 0, width, height);
			bgfx_touch(0);
			
			bgfx_dbg_text_clear(0, false);
			bgfx_dbg_text_image(Math.max(width / 2 / 8, 20) - 20, Math.max(height / 2 / 16, 6) - 6, 40, 12, logo, 160);
			bgfx_dbg_text_printf(0, 1, 0x1f, "bgfx/examples/25-c99");
			bgfx_dbg_text_printf(0, 2, 0x3f, "Description: Initialization and debug text with C99 API.");
			bgfx_dbg_text_printf(0, 3, 0x0f, "Color can be changed with ANSI \u001b[9;me\u001b[10;ms\u001b[11;mc\u001b[12;ma\u001b[13;mp\u001b[14;me\u001b[0m code too.");
			bgfx_dbg_text_printf(80, 4, 0x0f, "\u001b[;0m    \u001b[;1m    \u001b[; 2m    \u001b[; 3m    \u001b[; 4m    \u001b[; 5m    \u001b[; 6m    \u001b[; 7m    \u001b[0m");
			bgfx_dbg_text_printf(80, 5, 0x0f, "\u001b[;8m    \u001b[;9m    \u001b[;10m    \u001b[;11m    \u001b[;12m    \u001b[;13m    \u001b[;14m    \u001b[;15m    \u001b[0m");
			
			bgfx_frame(false);
		}
		
		bgfx_shutdown();
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}
	
}
