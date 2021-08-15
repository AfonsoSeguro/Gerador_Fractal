import os
import moviepy.video.io.ImageSequenceClip
import sys



def main():
		image_folder='FractalImages'
		
		image_files = os.listdir(image_folder)
		image_files.sort()

		for i in range(len(image_files)):
			image_files[i] = image_folder + "/" + image_files[i]
			
		
		clip = moviepy.video.io.ImageSequenceClip.ImageSequenceClip(image_files, fps=float(sys.argv[2]))
		clip.write_videofile(sys.argv[1])


		
		
main()

