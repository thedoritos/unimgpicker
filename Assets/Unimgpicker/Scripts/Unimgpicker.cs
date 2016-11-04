using UnityEngine;
using System.Collections;

namespace Kakera
{
    public class Unimgpicker : MonoBehaviour
    {
        public delegate void ImageDelegate(string url);

        public event ImageDelegate Completed;

        private static readonly string PickerClass = "com.kakeragames.unimgpicker.Picker";

        public void Show()
        {
            using (var picker = new AndroidJavaClass(PickerClass))
            {
                picker.CallStatic("show", "Select Image", "unimgpicker");
            }
        }

        public void OnComplete(string url)
        {
            Debug.Log("Unimgpicker.OnComplete url:" + url);

            var handler = Completed;
            if (handler != null)
            {
                handler("file://" + url);
            }
        }
    }
}