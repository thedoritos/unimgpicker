#if UNITY_EDITOR_OSX
using System.Runtime.InteropServices;
using UnityEditor;
using UnityEngine;
using System.IO;

namespace Kakera
{
	internal class PickerOSX_editor : IPicker
	{
		public void Show(string title, string outputFileName, int maxSize)
		{
			var path = EditorUtility.OpenFilePanel(title, "","png");
			if (path.Length != 0) {
				if (File.Exists(Application.persistentDataPath + "/" + outputFileName))
					File.Delete(Application.persistentDataPath + "/" + outputFileName);
				File.Copy(path, Application.persistentDataPath + "/" + outputFileName);
				Debug.Log (Application.persistentDataPath + "/" + outputFileName);
				var receiver = GameObject.Find("Unimgpicker");
				if (receiver != null)
				{
					receiver.SendMessage("OnComplete", path);
				}
			}
		}
	}
}
#endif