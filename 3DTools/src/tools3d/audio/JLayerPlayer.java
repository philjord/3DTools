package tools3d.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;

public class JLayerPlayer
{

	public static void playMP3(final String musicTrack)
	{

		Thread thread = new Thread(new Runnable()
		{

			public void run()
			{
				AudioInputStream din = null;
				try
				{

					AudioInputStream in = AudioSystem.getAudioInputStream(new File(musicTrack));
					AudioFormat baseFormat = in.getFormat();
					AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
							baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
					din = AudioSystem.getAudioInputStream(decodedFormat, in);
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
					SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
					if (line != null)
					{

						line.open(decodedFormat);

						// Adjust the volume on the output line.
						if (line.isControlSupported(FloatControl.Type.VOLUME))
						{
							FloatControl volume = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
							volume.setValue(-2F);
						}

						byte[] data = new byte[4096];
						// Start
						line.start();

						int nBytesRead;
						while ((nBytesRead = din.read(data, 0, data.length)) != -1)
						{
							line.write(data, 0, nBytesRead);
						}
						// Stop
						line.drain();
						line.stop();
						line.close();
						din.close();
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (din != null)
					{
						try
						{
							din.close();
						}
						catch (IOException e)
						{
						}
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	public static void setMasterOutputVolume(float value)
	{
		if (value < 0 || value > 1)
			throw new IllegalArgumentException("Volume can only be set to a value from 0 to 1. Given value is illegal: " + value);
		Line line = getMasterOutputLine();
		if (line == null)
			throw new RuntimeException("Master output port not found");
		boolean opened = open(line);
		try
		{
			FloatControl control = getVolumeControl(line);
			if (control == null)
				throw new RuntimeException("Volume control not found in master port: " + toString(line));
			control.setValue(value);
		}
		finally
		{
			if (opened)
				line.close();
		}
	}

	public static Float getMasterOutputVolume()
	{
		Line line = getMasterOutputLine();
		if (line == null)
			return null;
		boolean opened = open(line);
		try
		{
			FloatControl control = getVolumeControl(line);
			if (control == null)
				return null;
			return control.getValue();
		}
		finally
		{
			if (opened)
				line.close();
		}
	}

	public static void setMasterOutputMute(boolean value)
	{
		Line line = getMasterOutputLine();
		if (line == null)
			throw new RuntimeException("Master output port not found");
		boolean opened = open(line);
		try
		{
			BooleanControl control = getMuteControl(line);
			if (control == null)
				throw new RuntimeException("Mute control not found in master port: " + toString(line));
			control.setValue(value);
		}
		finally
		{
			if (opened)
				line.close();
		}
	}

	public static Boolean getMasterOutputMute()
	{
		Line line = getMasterOutputLine();
		if (line == null)
			return null;
		boolean opened = open(line);
		try
		{
			BooleanControl control = getMuteControl(line);
			if (control == null)
				return null;
			return control.getValue();
		}
		finally
		{
			if (opened)
				line.close();
		}
	}

	public static Line getMasterOutputLine()
	{
		for (Mixer mixer : getMixers())
		{
			for (Line line : getAvailableOutputLines(mixer))
			{
				if (line.getLineInfo().toString().contains("Master"))
					return line;
			}
		}
		return null;
	}

	public static FloatControl getVolumeControl(Line line)
	{
		if (!line.isOpen())
			throw new RuntimeException("Line is closed: " + toString(line));
		return (FloatControl) findControl(FloatControl.Type.VOLUME, line.getControls());
	}

	public static BooleanControl getMuteControl(Line line)
	{
		if (!line.isOpen())
			throw new RuntimeException("Line is closed: " + toString(line));
		return (BooleanControl) findControl(BooleanControl.Type.MUTE, line.getControls());
	}

	private static Control findControl(Type type, Control... controls)
	{
		if (controls == null || controls.length == 0)
			return null;
		for (Control control : controls)
		{
			if (control.getType().equals(type))
				return control;
			if (control instanceof CompoundControl)
			{
				CompoundControl compoundControl = (CompoundControl) control;
				Control member = findControl(type, compoundControl.getMemberControls());
				if (member != null)
					return member;
			}
		}
		return null;
	}

	public static List<Mixer> getMixers()
	{
		Info[] infos = AudioSystem.getMixerInfo();
		List<Mixer> mixers = new ArrayList<Mixer>(infos.length);
		for (Info info : infos)
		{
			Mixer mixer = AudioSystem.getMixer(info);
			mixers.add(mixer);
		}
		return mixers;
	}

	public static List<Line> getAvailableOutputLines(Mixer mixer)
	{
		return getAvailableLines(mixer, mixer.getTargetLineInfo());
	}

	public static List<Line> getAvailableInputLines(Mixer mixer)
	{
		return getAvailableLines(mixer, mixer.getSourceLineInfo());
	}

	private static List<Line> getAvailableLines(Mixer mixer, Line.Info[] lineInfos)
	{
		List<Line> lines = new ArrayList<Line>(lineInfos.length);
		for (Line.Info lineInfo : lineInfos)
		{
			Line line;
			line = getLineIfAvailable(mixer, lineInfo);
			if (line != null)
				lines.add(line);
		}
		return lines;
	}

	public static Line getLineIfAvailable(Mixer mixer, Line.Info lineInfo)
	{
		try
		{
			return mixer.getLine(lineInfo);
		}
		catch (LineUnavailableException ex)
		{
			return null;
		}
	}

	public static String getHierarchyInfo()
	{
		StringBuilder sb = new StringBuilder();
		for (Mixer mixer : getMixers())
		{
			sb.append("Mixer: ").append(toString(mixer)).append("\n");

			for (Line line : getAvailableOutputLines(mixer))
			{
				sb.append(" OUT: ").append(toString(line)).append("\n");
				boolean opened = open(line);
				for (Control control : line.getControls())
				{
					sb.append(" Control: ").append(toString(control)).append("\n");
					if (control instanceof CompoundControl)
					{
						CompoundControl compoundControl = (CompoundControl) control;
						for (Control subControl : compoundControl.getMemberControls())
						{
							sb.append(" Sub-Control: ").append(toString(subControl)).append("\n");
						}
					}
				}
				if (opened)
					line.close();
			}

			for (Line line : getAvailableOutputLines(mixer))
			{
				sb.append(" IN: ").append(toString(line)).append("\n");
				boolean opened = open(line);
				for (Control control : line.getControls())
				{
					sb.append(" Control: ").append(toString(control)).append("\n");
					if (control instanceof CompoundControl)
					{
						CompoundControl compoundControl = (CompoundControl) control;
						for (Control subControl : compoundControl.getMemberControls())
						{
							sb.append(" Sub-Control: ").append(toString(subControl)).append("\n");
						}
					}
				}
				if (opened)
					line.close();
			}

			sb.append("\n");
		}
		return sb.toString();
	}

	public static boolean open(Line line)
	{
		if (line.isOpen())
			return false;
		try
		{
			line.open();
		}
		catch (LineUnavailableException ex)
		{
			return false;
		}
		return true;
	}

	public static String toString(Control control)
	{
		if (control == null)
			return null;
		return control.toString() + " (" + control.getType().toString() + ")";
	}

	public static String toString(Line line)
	{
		if (line == null)
			return null;
		Line.Info info = line.getLineInfo();
		return info.toString();// + " (" + line.getClass().getSimpleName() + ")";
	}

	public static String toString(Mixer mixer)
	{
		if (mixer == null)
			return null;
		StringBuilder sb = new StringBuilder();
		Info info = mixer.getMixerInfo();
		sb.append(info.getName());
		sb.append(" (").append(info.getDescription()).append(")");
		sb.append(mixer.isOpen() ? " [open]" : " [closed]");
		return sb.toString();
	}

}
