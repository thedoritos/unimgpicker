using UnityEngine;
using System.Collections;

namespace Kakera
{
    public class Unimgpicker : MonoBehaviour
    {
        public delegate void ImageDelegate(string url);

        public event ImageDelegate Completed;

        private static readonly string PickerClass = "com.kakeragames.unimgpicker.Picker";

        public void Show(string title, string outputFileName)
        {
            using (var picker = new AndroidJavaClass(PickerClass))
            {
                picker.CallStatic("show", title, outputFileName);
            }
        }

        public void OnComplete(string url)
        {
            var handler = Completed;
            if (handler != null)
            {
                handler(url);
            }
        }
    }
}