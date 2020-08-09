using System;
using UnityEngine;

namespace Kakera
{
    public class Unimgpicker : MonoBehaviour
    {
        public delegate void ImageDelegate(string path);

        public delegate void ErrorDelegate(string message);

        public event ImageDelegate Completed;

        public event ErrorDelegate Failed;

        private IPicker picker =
#if UNITY_EDITOR
            new Picker_editor();
#elif UNITY_IOS
            new PickeriOS();
#elif UNITY_ANDROID
            new PickerAndroid();
#else
            new PickerUnsupported();
#endif

        [Obsolete("Resizing is deprecated. Use Show(title, outputFileName)")]
        public void Show(string title, string outputFileName, int maxSize)
        {
            Show(title, outputFileName);
        }

        public void Show(string title, string outputFileName)
        {
            picker.Show(title, outputFileName);
        }

        private void OnComplete(string path)
        {
            var handler = Completed;
            if (handler != null)
            {
                handler(path);
            }
        }

        private void OnFailure(string message)
        {
            var handler = Failed;
            if (handler != null)
            {
                handler(message);
            }
        }
    }
}