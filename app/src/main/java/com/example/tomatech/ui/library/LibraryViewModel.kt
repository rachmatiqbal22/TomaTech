package com.example.tomatech.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tomatech.R


data class Penyakit(val nama: String, val gambarResId: Int, val deskripsi: String)

class LibraryViewModel : ViewModel() {

    private val _penyakitList = MutableLiveData<List<Penyakit>>().apply {
        value = listOf(
            Penyakit("Healthy", R.drawable.healthy, "Healthy tomato leaves are vibrant, green, and free from visible signs of stress or disease. The leaf surface is smooth, with no discoloration, lesions, or abnormal growth. The plant is growing vigorously, indicating proper care and environmental conditions. Healthy leaves are essential for photosynthesis and overall plant productivity"),
            Penyakit("Bacterial Spot", R.drawable.bacterial_spot, "Bacterial spot is a common bacterial infection that affects tomato plants. It begins with small, water-soaked lesions on the leaf surface, which later turn dark brown or black, often with yellow halos. The lesions can coalesce, leading to extensive tissue damage. This disease is typically spread by splashing water, wind, or infected tools. If left untreated, it can cause significant defoliation and reduce the plant's ability to produce healthy fruit"),
            Penyakit("Early Blight", R.drawable.early_blight, "Early blight, caused by the fungus Alternaria solani, typically starts with small, dark, circular spots with concentric rings, often on the lower leaves of the plant. These spots enlarge over time, causing the leaf tissue to die and leading to the development of large, irregular lesions. As the disease progresses, it can move upwards, affecting the entire plant and resulting in premature leaf drop. Early blight thrives in humid conditions and is a serious threat to tomato crops, especially during warm, wet periods"),
            Penyakit("Late Blightt", R.drawable.late_blightt, "Description: Late blight is one of the most destructive diseases for tomatoes, caused by the oomycete Phytophthora infestans. It starts with dark, water-soaked lesions on the leaves, which quickly expand. These lesions are often accompanied by a white, fuzzy growth on the underside of the leaf, which is a key indicator of late blight. This disease spreads rapidly in cool, wet conditions and can decimate entire tomato crops if not controlled. Infected plants can exhibit rapid leaf and fruit decay, making early detection crucial to prevent widespread damage"),
            Penyakit("Leaf Mold", R.drawable.leaf_mold, "Description: Leaf mold, caused by the fungus Cladosporium fulvum, primarily affects the upper surface of tomato leaves. The initial symptoms are yellowish patches, followed by the development of a grayish or white mold on the underside of the leaves. This disease thrives in humid conditions, particularly in greenhouses, and can cause leaf curling, premature leaf drop, and reduced photosynthesis. Infected leaves can eventually become weak and fall off, significantly affecting plant health and fruit production")
        )
    }
    val penyakitList: LiveData<List<Penyakit>> = _penyakitList
}
