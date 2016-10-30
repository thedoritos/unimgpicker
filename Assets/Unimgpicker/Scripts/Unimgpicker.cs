using UnityEngine;
using System.Collections;

namespace Kakera
{
    public class Unimgpicker : MonoBehaviour
    {
        private static readonly string PickerClass = "com.kakeragames.unimgpicker.Picker";

        public void Show()
        {
            using (var picker = new AndroidJavaClass(PickerClass))
            {
                picker.CallStatic("show", "Select Image");
            }
        }

        public void OnComplete(string url)
        {
            Debug.Log("Unimgpicker.OnComplete url:" + url);
        }
    }
}