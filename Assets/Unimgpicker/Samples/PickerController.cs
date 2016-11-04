using UnityEngine;
using UnityEngine.UI;
using System.Collections;

namespace Kakera
{
    public class PickerController : MonoBehaviour
    {
        [SerializeField]
        private Image image;

        [SerializeField]
        private Unimgpicker imagePicker;

        void Awake()
        {
            imagePicker.Completed += (string url) =>
            {
                StartCoroutine(LoadImage(url, image));
            };
        }

        public void OnPressShowPicker()
        {
            imagePicker.Show("Select Image", "unimgpicker");
        }

        private IEnumerator LoadImage(string url, Image output)
        {
            var www = new WWW(url);
            yield return www;

            var texture = www.texture;
            if (texture == null)
            {
                Debug.LogError("Failed to load texture url:" + url);
            }

            output.sprite = Sprite.Create(texture, new Rect(0, 0, texture.width, texture.height), Vector2.zero);
        }
    }
}