using UnityEngine;
using UnityEditor;
using UnityEditor.Callbacks;
using UnityEditor.iOS.Xcode;
using System.Diagnostics;
using System.IO;
using Debug = UnityEngine.Debug;

namespace Kakera.Unimgpicker
{
    public class XcodeProjectConfigurator
    {
        [PostProcessBuild]
        static void OnPostprocessBuild(BuildTarget buildTarget, string buildPath)
        {
            if (buildTarget != BuildTarget.iOS)
                return;

            ConfigurePlist(buildPath, "Info.plist");
        }

        static void ConfigurePlist(string buildPath, string plistPath)
        {
            var plist = new PlistDocument();
            var path = Path.Combine(buildPath, plistPath);

            plist.ReadFromFile(path);

            var descriptionFilePath = Path.Combine(Application.dataPath, "Unimgpicker/Editor/NSPhotoLibraryUsageDescription.txt");
            if (!File.Exists(descriptionFilePath))
            {
                Debug.LogError(string.Format("[Unimgpicker]:File {0} is not found.", descriptionFilePath));
                return;
            }

            var description = File.ReadAllText(descriptionFilePath);

            plist.root.SetString("NSPhotoLibraryUsageDescription", description);
            Debug.Log(string.Format("[Unimgpicker]:Set NSPhotoLibraryUsageDescription as \"{0}\"", description));

            plist.WriteToFile(path);
        }
    }
}