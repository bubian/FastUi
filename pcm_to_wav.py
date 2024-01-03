import wave
import sys
import array

if len(sys.argv) < 2:
    print("Usage: python pcm_to_wav.py <input_file>")
    sys.exit(1)

input_file = sys.argv[1]

with open(input_file, "rb") as file:
    data = file.read()
    samples = array.array("h", data)
    wav_file = wave.open(input_file + ".wav", "wb")
    wav_file.setparams((1, 2, 16000, 0, 'NONE', 'not compressed'))
    wav_file.writeframes(samples.tobytes())
    wav_file.close()

print(f"Converted {input_file} to {input_file}.wav")