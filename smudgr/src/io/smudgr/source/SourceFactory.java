package io.smudgr.source;

public class SourceFactory {

	public Source makeSource(String path) {
		String ext = path.substring(path.lastIndexOf(".") + 1);

		try {
			switch (ext) {
			case "mov":
			case "mp4":
				return new Video(path);
			case "gif":
				return new Gif(path);
			case "png":
			case "jpg":
			case "jpeg":
				return new Image(path);
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

}
