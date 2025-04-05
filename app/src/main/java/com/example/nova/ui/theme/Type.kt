//ICS342 - NICK KELLEY
package com.example.nova.ui.theme

//imports
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import com.example.nova.R


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */

)

//added some custom fonts B)
//mochi is the only one im using right now, but might use the others at some point...
//val FiraCodeFont = FontFamily(
    //Font(R.font.firacode)
//)
val MochiPopOne = FontFamily(
    Font(R.font.mochipopone)
)
//val YuseiMagic = FontFamily(
    //Font(R.font.yuseimagic)
//)
