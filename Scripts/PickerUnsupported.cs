using UnityEngine;

namespace Kakera
{
    internal class PickerUnsupported : IPicker
    {
        public void Show(string title, string outputFileName)
        {
            var message = "Unimgpicker is not supported on this platform.";
            Debug.LogError(message);

            var receiver = GameObject.Find("Unimgpicker");
            if (receiver != null)
            {
                receiver.SendMessage("OnFailure", message);
            }
        }
    }
}