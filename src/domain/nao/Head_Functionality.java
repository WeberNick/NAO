package domain.nao;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aljoscha Narr
 * @see http://doc.aldebaran.com/2-1/naoqi/index.html#naoqi-api
 */
@SuppressWarnings("unchecked")
public class Head_Functionality {
	static ArrayList<Float> list;
	static ALMemory mem;
	private boolean stopEvent;
	private ArrayList<Object> board;
	private NAO_Handler nao_handler;

	public Head_Functionality(NAO_Handler nao_handler) {
		this.nao_handler = nao_handler;
	}

	/**
	 * The method detect the first six fields and if successfully return true
	 * 
	 * @return true if the playboard is detected successfully, false otherwise
	 * @deprecated not in use anymore
	 */

	@Deprecated
	boolean detectPlayboard() throws Exception {
		ALMotion alm = new ALMotion(nao_handler.getSession());
		ALTracker alt = new ALTracker(nao_handler.getSession());
		ALColorBlobDetection blob = new ALColorBlobDetection(
				nao_handler.getSession());
		board = new ArrayList<Object>();
		List<Float> position = new ArrayList<Float>();
		boolean succes = true;
		for (int i = 0; i <= 5; i++) {
			alm.wakeUp();
			if (i == 0) {
				position = alm.getAngles("Head", false);
				position.set(0, 1f);
				position.set(1, 0.4f);
				alm.angleInterpolationWithSpeed("Head", position, 0.02f);
				position = getCirclePosition(0, "black", true);
				if (position.size() == 0)
					return false;
				board.add(position);
			}
			if (i == 1) {
				position = alm.getAngles("Head", false);
				position.set(0, 0f);
				position.set(1, 0f);
				alm.angleInterpolationWithSpeed("Head", position, 0.2f);
				position = alm.getAngles("Head", false);
				position.set(0, 0.6f);
				position.set(1, 0.285f);
				alm.angleInterpolationWithSpeed("Head", position, 0.02f);
				position = getCirclePosition(0, "black", true);
				if (position.size() == 0)
					return false;
				board.add(position);
			}
			if (i == 2) {
				position = alm.getAngles("Head", false);
				position.set(0, 0.45f);
				position.set(1, -0.085f);
				alm.angleInterpolationWithSpeed("Head", position, 0.02f);
				position = getCirclePosition(0, "black", true);
				if (position.size() == 0)
					return false;
				board.add(position);
			}
			if (i == 3) {
				alm.wakeUp();
				position = alm.getAngles("Head", false);
				position.set(0, 0f);
				position.set(1, 0f);
				alm.angleInterpolationWithSpeed("Head", position, 0.02f);
				blob.setColor(0, 0, 0, 4);// orange circle
				blob.setObjectProperties(10, 0.05f, "Circle");
				alt.trackEvent("ALTracker/ColorBlobDetected");
				position = alt.getTargetPosition(2);
				alt.stopTracker();
				if (position.size() == 0)
					return false;
				board.add(position);
			}
			if (i == 4) {
				position = alm.getAngles("Head", false);
				position.set(0, -0.5f);
				position.set(1, 0.4f);
				alm.angleInterpolationWithSpeed("Head", position, 0.02f);
				position = getCirclePosition(0, "black", true);
				if (position.size() == 0)
					return false;
				board.add(position);
			}
			if (i == 5) {
				position = alm.getAngles("Head", false);
				position.set(0, -1f);
				position.set(1, 0.4f);
				alm.angleInterpolationWithSpeed("Head", position, 0.02f);
				position = getCirclePosition(0, "black", true);
				if (position.size() == 0)
					return false;
				board.add(position);
			}
		}
		return succes;
	}

	/**
	 * 
	 * @param int 0 for top camera and 1 for bottom camera
	 * @return BufferedImage Image of the Camera
	 * @see BufferedImage
	 * @see ByteBuffer
	 */

	 public BufferedImage getCameraStream(int camera) throws Exception {
		ALVideoDevice videoDevice = new ALVideoDevice(nao_handler.getSession());
		String pic_nr = "" + System.nanoTime();
		String moduleName = videoDevice.subscribeCamera(pic_nr, camera, 1, 11,
				10);
		List<Object> video_container = (List<Object>) videoDevice
				.getImageRemote(moduleName);
		ByteBuffer buffer = (ByteBuffer) video_container.get(6);
		byte[] binaryImage = buffer.array();
		videoDevice.releaseImage(pic_nr);
		videoDevice.unsubscribe(pic_nr);
		int[] intArray;
		intArray = new int[320 * 240];
		for (int i = 0; i < 320 * 240; i++) {
			intArray[i] = ((255 & 0xFF) << 24) | // alpha
					((binaryImage[i * 3 + 0] & 0xFF) << 16) | // red
					((binaryImage[i * 3 + 1] & 0xFF) << 8) | // green
					((binaryImage[i * 3 + 2] & 0xFF) << 0); // blue
		}
		BufferedImage img = new BufferedImage(320, 240,
				BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, 320, 240, intArray, 0, 320);
		return img;
	}

	ArrayList<Float> getCirclePosition(int camera, String color)
			throws Exception {
		return getCirclePosition(camera, color, true);
	}

	/**
	 * The method return the position between robot and circle
	 * @param camera 0 for top camera and 1 for bottom camera
	 * @param color color of the circle
	 * @param look true if the robot should look at and false otherwise
	 * @return ArrayList<Float> the position (x,y,dz/theta) between robot and given circle in the FRAME_ROBOT
	 */
	ArrayList<Float> getCirclePosition(int camera, String color,
			boolean look) throws Exception {
		ALTracker alt = new ALTracker(nao_handler.getSession());
		ALColorBlobDetection blob = new ALColorBlobDetection(
				nao_handler.getSession());
		ALMotion alm = new ALMotion(nao_handler.getSession());
		list = new ArrayList<Float>();
		stopEvent = true;
		mem = new ALMemory(nao_handler.getSession());
		pause();
		switch (color) {

		case "orange":
			blob.setColor(255, 173, 1, 20);// orange circle
			break;
		case "beige":
			blob.setColor(198, 191, 113, 20);// beige circle
			break;
		case "green":
			blob.setColor(0, 255, 24, 20);// green circle
			break;
		case "khaki":
			blob.setColor(191, 239, 48, 20);// khaki circle
			break;
		case "grey":
			blob.setColor(121, 121, 121, 10);// grey circle
			break;
		case "pink":
			blob.setColor(255, 128, 129, 20);// pink circle
			break;
		case "blue":
			blob.setColor(129, 255, 254, 20);// blue circle
			break;
		case "purple":
			blob.setColor(127, 0, 255, 20);// purple circle
			break;
		case "red":
			blob.setColor(114, 0, 0, 20);// dark red circle
			break;
		case "black":
			blob.setColor(0, 0, 0, 4);// black circle
			break;
		default:
			System.out.println("Wrong color");
			break;
		}
		blob.setActiveCamera(camera);
		if (camera == 0)
			if (color.equals("black"))
				blob.setObjectProperties(10, 0.05f, "Circle");// black
			else {
				blob.setObjectProperties(30, 0.172f, "Circle");// else
			}
		if (camera == 1) {
			if (color.equals("black"))
				blob.setObjectProperties(10, 0.061f, "Circle");
			else {
				blob.setObjectProperties(30, 0.24f, "Circle");// orange
			}
		}
		alt.trackEvent("ALTracker/ColorBlobDetected");
		mem.subscribeToEvent("ALTracker/ColorBlobDetected",
				new EventCallback<Object>() {
					public void onEvent(Object x) throws InterruptedException,
							CallError {
						if (stopEvent) {
							ArrayList<Object> event = (ArrayList<Object>) x;
							list.add(((ArrayList<Float>) event.get(1)).get(0));
							list.add(((ArrayList<Float>) event.get(1)).get(1));
							list.add(((ArrayList<Float>) event.get(1)).get(5));
							stopEvent = false;
						}
					}
				});
		if (alt.getTargetPosition(2).size() > 0 && look) {
			alm.wakeUp();
			alt.lookAt(alt.getTargetPosition(2), 2, 0.2f, false);
		}
		if (!look) {
			alm.wakeUp();
		}
		mem.unsubscribeAllEvents();
		alt.stopTracker();
		return list;
	}

	/**
	 * The method pause all head movements
	 */

	void pause() {
		while (nao_handler.getPause()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("Thread was interrupted");
			}
		}
	}

}
