using UnityEngine;
using UnityEngine.UI;
using System.Collections;

namespace Kakera
{
	public class PickerController_RotationCheck : MonoBehaviour
	{
		[SerializeField]
		private Unimgpicker imagePicker;

		[SerializeField]
		private RawImage imageRenderer;

		void Awake()
		{
			imagePicker.Completed += (string path) =>
			{
				StartCoroutine(LoadImage(path, imageRenderer));
			};
		}

		public void OnPressShowPicker()
		{
			imagePicker.Show("Select Image", "unimgpicker", 1024);
		}

		private IEnumerator LoadImage(string path, RawImage output)
		{
			var url = "file://" + path;
			var www = new WWW(url);
			yield return www;

			var texture = www.texture;
			if (texture == null)
			{
				Debug.LogError("Failed to load texture url:" + url);
			}

			output.texture = texture;

			var size = output.rectTransform.sizeDelta;
			size.y = (float)texture.height / (float)texture.width * size.x;
			output.rectTransform.sizeDelta = size;
		}
	}
}