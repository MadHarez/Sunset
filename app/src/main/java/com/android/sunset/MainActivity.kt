package com.android.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: View
    private lateinit var sunView: ImageView
    private lateinit var sunRaysView: ImageView
    private lateinit var skyView: View
    private lateinit var moonView: ImageView
    private lateinit var stars: List<ImageView>
    private lateinit var clouds: List<ImageView>
    
    // Ocean elements
    private lateinit var seaView: View
    private lateinit var sunReflection: ImageView
    private lateinit var moonReflection: ImageView
    private lateinit var starReflections: List<ImageView>
    private lateinit var waves: List<View>

    private var isDaytime = true
    private var isAnimating = false
    private var useAlternateAnimation = false

    private val blueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val sunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val nightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById(R.id.scene)
        sunView = findViewById(R.id.sun)
        sunRaysView = findViewById(R.id.sun_rays)
        skyView = findViewById(R.id.sky)
        moonView = findViewById(R.id.moon)

        stars = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5),
            findViewById(R.id.star6)
        )

        clouds = listOf(
            findViewById(R.id.cloud1),
            findViewById(R.id.cloud2),
            findViewById(R.id.cloud3)
        )
        
        // Ocean elements
        seaView = findViewById(R.id.sea)
        sunReflection = findViewById(R.id.sun_reflection)
        moonReflection = findViewById(R.id.moon_reflection)
        
        starReflections = listOf(
            findViewById(R.id.star1_reflection),
            findViewById(R.id.star2_reflection),
            findViewById(R.id.star3_reflection),
            findViewById(R.id.star4_reflection),
            findViewById(R.id.star5_reflection),
            findViewById(R.id.star6_reflection)
        )
        
        waves = listOf(
            findViewById(R.id.wave1),
            findViewById(R.id.wave2),
            findViewById(R.id.wave3)
        )

        // Show clouds initially
        clouds.forEach { it.alpha = 1f }
        
        // Start continuous wave animation
        startWaveAnimation()

        sceneView.setOnClickListener {
            if (!isAnimating) {
                if (isDaytime) {
                    if (useAlternateAnimation) {
                        startAlternateSunsetAnimation()
                    } else {
                        startSunsetAnimation()
                    }
                } else {
                    if (useAlternateAnimation) {
                        startAlternateSunriseAnimation()
                    } else {
                        startSunriseAnimation()
                    }
                }
                // Toggle animation style for next time
                useAlternateAnimation = !useAlternateAnimation
            }
        }
    }

    private fun startSunsetAnimation() {
        isAnimating = true
        val sunYStart = sunView.top.toFloat()
        val sunYEnd = skyView.height.toFloat()

        // Sun movement down
        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        heightAnimator.interpolator = AccelerateInterpolator()

        // Sun pulsing effect (scale)
        val sunScaleXDown = ObjectAnimator.ofFloat(sunView, "scaleX", 1f, 0.8f, 1.2f, 0.9f, 1f)
            .setDuration(3000)
        val sunScaleYDown = ObjectAnimator.ofFloat(sunView, "scaleY", 1f, 0.8f, 1.2f, 0.9f, 1f)
            .setDuration(3000)

        // Sun rotation
        val sunRotation = ObjectAnimator.ofFloat(sunView, "rotation", 0f, 360f)
            .setDuration(3000)
        sunRotation.interpolator = LinearInterpolator()

        // Sky color transitions
        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
            .setDuration(3000)
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, nightSkyColor)
            .setDuration(1500)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())

        // Hide clouds
        val cloudAnimators = clouds.map { cloud ->
            ObjectAnimator.ofFloat(cloud, "alpha", 1f, 0f).setDuration(2000)
        }

        // Show stars with twinkling effect
        val starAnimators = stars.mapIndexed { index, star ->
            ObjectAnimator.ofFloat(star, "alpha", 0f, 1f)
                .setDuration(1000)
                .apply { startDelay = 2500L + index * 100L }
        }

        // Star twinkling
        val starTwinkle = stars.map { star ->
            ObjectAnimator.ofFloat(star, "scaleX", 1f, 1.3f, 1f)
                .setDuration(800)
                .apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    startDelay = (Math.random() * 1000).toLong()
                }
        }

        val starTwinkleY = stars.map { star ->
            ObjectAnimator.ofFloat(star, "scaleY", 1f, 1.3f, 1f)
                .setDuration(800)
                .apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    startDelay = (Math.random() * 1000).toLong()
                }
        }

        // Sun rays animation - rotating and fading
        val sunRaysRotation = ObjectAnimator.ofFloat(sunRaysView, "rotation", 0f, 360f)
            .setDuration(3000)
        sunRaysRotation.interpolator = LinearInterpolator()
        
        val sunRaysFadeIn = ObjectAnimator.ofFloat(sunRaysView, "alpha", 0f, 0.8f)
            .setDuration(1000)
        
        val sunRaysFadeOut = ObjectAnimator.ofFloat(sunRaysView, "alpha", 0.8f, 0f)
            .setDuration(1500)
        sunRaysFadeOut.startDelay = 1500
        
        val sunRaysPulse = ObjectAnimator.ofFloat(sunRaysView, "scaleX", 1f, 1.2f, 1f)
            .setDuration(3000)
        val sunRaysPulseY = ObjectAnimator.ofFloat(sunRaysView, "scaleY", 1f, 1.2f, 1f)
            .setDuration(3000)
        
        // Show moon
        val moonAnimator = ObjectAnimator.ofFloat(moonView, "alpha", 0f, 1f)
            .setDuration(1500)
        moonAnimator.startDelay = 3000
        
        // Sun reflection animation
        val sunReflectionY = ObjectAnimator.ofFloat(sunReflection, "translationY", 0f, 60f)
            .setDuration(3000)
        sunReflectionY.interpolator = AccelerateInterpolator()
        
        val sunReflectionAlpha = ObjectAnimator.ofFloat(sunReflection, "alpha", 0.6f, 0.3f, 0f)
            .setDuration(3000)
        
        val sunReflectionScale = ObjectAnimator.ofFloat(sunReflection, "scaleY", 0.8f, 0.5f)
            .setDuration(3000)
        
        // Moon reflection animation
        val moonReflectionAnimator = ObjectAnimator.ofFloat(moonReflection, "alpha", 0f, 0.5f)
            .setDuration(1500)
        moonReflectionAnimator.startDelay = 3000
        
        // Star reflections animation
        val starReflectionAnimators = starReflections.mapIndexed { index, starReflection ->
            ObjectAnimator.ofFloat(starReflection, "alpha", 0f, 0.4f)
                .setDuration(1000)
                .apply { startDelay = 2500L + index * 100L }
        }
        
        // Star reflection twinkling
        val starReflectionTwinkle = starReflections.map { starReflection ->
            ObjectAnimator.ofFloat(starReflection, "alpha", 0.3f, 0.5f)
                .setDuration(1000)
                .apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    startDelay = (Math.random() * 1000).toLong() + 3000L
                }
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunScaleXDown)
            .with(sunScaleYDown)
            .with(sunRotation)
            .with(sunsetSkyAnimator)
            .with(sunReflectionY)
            .with(sunReflectionAlpha)
            .with(sunReflectionScale)
            .with(sunRaysRotation)
            .with(sunRaysPulse)
            .with(sunRaysPulseY)
            .before(nightSkyAnimator)

        cloudAnimators.forEach { animatorSet.play(heightAnimator).with(it) }
        starAnimators.forEach { animatorSet.play(nightSkyAnimator).before(it) }
        starReflectionAnimators.forEach { it.start() }

        animatorSet.start()
        sunRaysFadeIn.start()
        sunRaysFadeOut.start()
        moonAnimator.start()
        moonReflectionAnimator.start()
        starTwinkle.forEach { it.start() }
        starTwinkleY.forEach { it.start() }
        starReflectionTwinkle.forEach { it.start() }

        animatorSet.doOnEnd {
            isAnimating = false
            isDaytime = false
        }
    }

    private fun startSunriseAnimation() {
        isAnimating = true
        val sunYEnd = sunView.top.toFloat()
        val sunYStart = skyView.height.toFloat()

        // Sun movement up
        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        heightAnimator.interpolator = DecelerateInterpolator()

        // Sun pulsing effect (scale)
        val sunScaleXUp = ObjectAnimator.ofFloat(sunView, "scaleX", 1f, 1.3f, 0.9f, 1.1f, 1f)
            .setDuration(3000)
        val sunScaleYUp = ObjectAnimator.ofFloat(sunView, "scaleY", 1f, 1.3f, 0.9f, 1.1f, 1f)
            .setDuration(3000)

        // Sun rotation
        val sunRotation = ObjectAnimator.ofFloat(sunView, "rotation", 0f, -360f)
            .setDuration(3000)
        sunRotation.interpolator = LinearInterpolator()

        // Sky color transitions
        val sunriseSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", nightSkyColor, sunsetSkyColor)
            .setDuration(1500)
        sunriseSkyAnimator.setEvaluator(ArgbEvaluator())

        val daySkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, blueSkyColor)
            .setDuration(3000)
        daySkyAnimator.setEvaluator(ArgbEvaluator())

        // Hide stars
        val starAnimators = stars.map { star ->
            ObjectAnimator.ofFloat(star, "alpha", 1f, 0f).setDuration(1000)
        }

        // Hide moon
        val moonAnimator = ObjectAnimator.ofFloat(moonView, "alpha", 1f, 0f)
            .setDuration(1000)

        // Show clouds
        val cloudAnimators = clouds.mapIndexed { index, cloud ->
            ObjectAnimator.ofFloat(cloud, "alpha", 0f, 1f)
                .setDuration(1500)
                .apply { startDelay = 2000L + index * 200L }
        }
        
        // Sun rays animation - rotating and fading in
        val sunRaysRotation = ObjectAnimator.ofFloat(sunRaysView, "rotation", 0f, -360f)
            .setDuration(3000)
        sunRaysRotation.interpolator = LinearInterpolator()
        
        val sunRaysFadeIn = ObjectAnimator.ofFloat(sunRaysView, "alpha", 0f, 0.8f)
            .setDuration(1500)
        sunRaysFadeIn.startDelay = 1500
        
        val sunRaysFadeOut = ObjectAnimator.ofFloat(sunRaysView, "alpha", 0.8f, 0f)
            .setDuration(1000)
        sunRaysFadeOut.startDelay = 2500
        
        val sunRaysPulse = ObjectAnimator.ofFloat(sunRaysView, "scaleX", 1f, 1.2f, 1f)
            .setDuration(3000)
        val sunRaysPulseY = ObjectAnimator.ofFloat(sunRaysView, "scaleY", 1f, 1.2f, 1f)
            .setDuration(3000)
        
        // Sun reflection animation (rising)
        val sunReflectionY = ObjectAnimator.ofFloat(sunReflection, "translationY", 60f, 0f)
            .setDuration(3000)
        sunReflectionY.interpolator = DecelerateInterpolator()
        
        val sunReflectionAlpha = ObjectAnimator.ofFloat(sunReflection, "alpha", 0f, 0.3f, 0.6f)
            .setDuration(3000)
        
        val sunReflectionScale = ObjectAnimator.ofFloat(sunReflection, "scaleY", 0.5f, 0.8f)
            .setDuration(3000)
        
        // Hide moon reflection
        val moonReflectionAnimator = ObjectAnimator.ofFloat(moonReflection, "alpha", 0.5f, 0f)
            .setDuration(1000)
        
        // Hide star reflections
        val starReflectionAnimators = starReflections.map { starReflection ->
            ObjectAnimator.ofFloat(starReflection, "alpha", 0.4f, 0f).setDuration(1000)
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(sunriseSkyAnimator)
            .before(heightAnimator)
        animatorSet.play(heightAnimator)
            .with(sunScaleXUp)
            .with(sunScaleYUp)
            .with(sunRotation)
            .with(daySkyAnimator)
            .with(sunReflectionY)
            .with(sunReflectionAlpha)
            .with(sunReflectionScale)
            .with(sunRaysRotation)
            .with(sunRaysPulse)
            .with(sunRaysPulseY)

        starAnimators.forEach { animatorSet.play(sunriseSkyAnimator).with(it) }
        cloudAnimators.forEach { animatorSet.play(daySkyAnimator).with(it) }
        starReflectionAnimators.forEach { it.start() }

        animatorSet.start()
        sunRaysFadeIn.start()
        sunRaysFadeOut.start()
        moonAnimator.start()
        moonReflectionAnimator.start()

        animatorSet.doOnEnd {
            isAnimating = false
            isDaytime = true
        }
    }

    private fun startAlternateSunsetAnimation() {
        isAnimating = true
        val sunYStart = sunView.top.toFloat()
        val sunYEnd = skyView.height.toFloat()

        // Sun movement with bounce effect
        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunYStart, sunYEnd)
            .setDuration(3500)
        heightAnimator.interpolator = AccelerateInterpolator(1.5f)

        // Spiral effect - combine rotation and scale
        val sunRotation = ObjectAnimator.ofFloat(sunView, "rotation", 0f, 720f)
            .setDuration(3500)
        sunRotation.interpolator = LinearInterpolator()

        // Pulsing with different pattern
        val sunScaleX = ObjectAnimator.ofFloat(sunView, "scaleX", 1f, 1.5f, 0.7f, 1.2f, 0.5f)
            .setDuration(3500)
        val sunScaleY = ObjectAnimator.ofFloat(sunView, "scaleY", 1f, 1.5f, 0.7f, 1.2f, 0.5f)
            .setDuration(3500)

        // Alpha fade
        val sunAlpha = ObjectAnimator.ofFloat(sunView, "alpha", 1f, 0.8f, 1f, 0.6f, 0.3f)
            .setDuration(3500)

        // Sky transitions with different timing
        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
            .setDuration(2500)
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, nightSkyColor)
            .setDuration(2000)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())

        // Clouds fade and move
        val cloudAnimators = clouds.mapIndexed { index, cloud ->
            val fadeOut = ObjectAnimator.ofFloat(cloud, "alpha", 1f, 0f).setDuration(2500)
            val moveUp = ObjectAnimator.ofFloat(cloud, "translationY", 0f, -100f).setDuration(2500)
            AnimatorSet().apply {
                playTogether(fadeOut, moveUp)
            }
        }

        // Stars appear with cascade effect
        val starAnimators = stars.mapIndexed { index, star ->
            ObjectAnimator.ofFloat(star, "alpha", 0f, 1f)
                .setDuration(800)
                .apply { startDelay = 2800L + index * 150L }
        }

        // Moon rises
        val moonRise = ObjectAnimator.ofFloat(moonView, "translationY", 100f, 0f)
            .setDuration(2000)
        val moonFade = ObjectAnimator.ofFloat(moonView, "alpha", 0f, 1f)
            .setDuration(2000)
        val moonSet = AnimatorSet().apply {
            playTogether(moonRise, moonFade)
            startDelay = 2500
        }
        
        // Sun rays animation - double rotation with spiral effect
        val sunRaysRotation = ObjectAnimator.ofFloat(sunRaysView, "rotation", 0f, 720f)
            .setDuration(3500)
        sunRaysRotation.interpolator = LinearInterpolator()
        
        val sunRaysFadeIn = ObjectAnimator.ofFloat(sunRaysView, "alpha", 0f, 1f)
            .setDuration(1200)
        
        val sunRaysFadeOut = ObjectAnimator.ofFloat(sunRaysView, "alpha", 1f, 0f)
            .setDuration(1500)
        sunRaysFadeOut.startDelay = 2000
        
        val sunRaysPulse = ObjectAnimator.ofFloat(sunRaysView, "scaleX", 1f, 1.5f, 0.8f, 1.3f, 0.5f)
            .setDuration(3500)
        val sunRaysPulseY = ObjectAnimator.ofFloat(sunRaysView, "scaleY", 1f, 1.5f, 0.8f, 1.3f, 0.5f)
            .setDuration(3500)
        
        // Sun reflection with spiral effect
        val sunReflectionY = ObjectAnimator.ofFloat(sunReflection, "translationY", 0f, 80f)
            .setDuration(3500)
        sunReflectionY.interpolator = AccelerateInterpolator(1.5f)
        
        val sunReflectionAlpha = ObjectAnimator.ofFloat(sunReflection, "alpha", 0.6f, 0.4f, 0.6f, 0.2f, 0f)
            .setDuration(3500)
        
        val sunReflectionScale = ObjectAnimator.ofFloat(sunReflection, "scaleY", 0.8f, 0.3f)
            .setDuration(3500)
        
        // Moon reflection rises
        val moonReflectionRise = ObjectAnimator.ofFloat(moonReflection, "translationY", 100f, 0f)
            .setDuration(2000)
        val moonReflectionFade = ObjectAnimator.ofFloat(moonReflection, "alpha", 0f, 0.5f)
            .setDuration(2000)
        val moonReflectionSet = AnimatorSet().apply {
            playTogether(moonReflectionRise, moonReflectionFade)
            startDelay = 2500
        }
        
        // Star reflections appear
        val starReflectionAnimators = starReflections.mapIndexed { index, starReflection ->
            ObjectAnimator.ofFloat(starReflection, "alpha", 0f, 0.4f)
                .setDuration(800)
                .apply { startDelay = 2800L + index * 150L }
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunRotation)
            .with(sunScaleX)
            .with(sunScaleY)
            .with(sunAlpha)
            .with(sunsetSkyAnimator)
            .with(sunReflectionY)
            .with(sunReflectionAlpha)
            .with(sunReflectionScale)
            .with(sunRaysRotation)
            .with(sunRaysPulse)
            .with(sunRaysPulseY)
            .before(nightSkyAnimator)

        cloudAnimators.forEach { it.start() }
        starAnimators.forEach { it.start() }
        starReflectionAnimators.forEach { it.start() }

        animatorSet.start()
        sunRaysFadeIn.start()
        sunRaysFadeOut.start()
        moonSet.start()
        moonReflectionSet.start()

        animatorSet.doOnEnd {
            isAnimating = false
            isDaytime = false
            sunView.alpha = 1f // Reset alpha for next animation
        }
    }

    private fun startAlternateSunriseAnimation() {
        isAnimating = true
        val sunYEnd = sunView.top.toFloat()
        val sunYStart = skyView.height.toFloat()

        // Sun rises with spring effect
        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunYStart, sunYEnd)
            .setDuration(3500)
        heightAnimator.interpolator = DecelerateInterpolator(2f)

        // Reverse spiral
        val sunRotation = ObjectAnimator.ofFloat(sunView, "rotation", 0f, -540f)
            .setDuration(3500)
        sunRotation.interpolator = LinearInterpolator()

        // Growing effect
        val sunScaleX = ObjectAnimator.ofFloat(sunView, "scaleX", 0.3f, 0.8f, 1.3f, 0.9f, 1f)
            .setDuration(3500)
        val sunScaleY = ObjectAnimator.ofFloat(sunView, "scaleY", 0.3f, 0.8f, 1.3f, 0.9f, 1f)
            .setDuration(3500)

        // Alpha fade in
        val sunAlpha = ObjectAnimator.ofFloat(sunView, "alpha", 0.3f, 0.6f, 1f, 0.8f, 1f)
            .setDuration(3500)

        // Sky transitions
        val sunriseSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", nightSkyColor, sunsetSkyColor)
            .setDuration(2000)
        sunriseSkyAnimator.setEvaluator(ArgbEvaluator())

        val daySkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, blueSkyColor)
            .setDuration(2500)
        daySkyAnimator.setEvaluator(ArgbEvaluator())

        // Stars disappear quickly
        val starAnimators = stars.map { star ->
            ObjectAnimator.ofFloat(star, "alpha", 1f, 0f).setDuration(800)
        }

        // Moon sets
        val moonSet = ObjectAnimator.ofFloat(moonView, "translationY", 0f, 100f)
            .setDuration(1500)
        val moonFade = ObjectAnimator.ofFloat(moonView, "alpha", 1f, 0f)
            .setDuration(1500)
        val moonAnimSet = AnimatorSet().apply {
            playTogether(moonSet, moonFade)
        }

        // Clouds appear and drift in
        val cloudAnimators = clouds.mapIndexed { index, cloud ->
            val fadeIn = ObjectAnimator.ofFloat(cloud, "alpha", 0f, 1f).setDuration(2000)
            val moveDown = ObjectAnimator.ofFloat(cloud, "translationY", -100f, 0f).setDuration(2000)
            AnimatorSet().apply {
                playTogether(fadeIn, moveDown)
                startDelay = 2500L + index * 250L
            }
        }
        
        // Sun rays animation - reverse spiral with growing effect
        val sunRaysRotation = ObjectAnimator.ofFloat(sunRaysView, "rotation", 0f, -540f)
            .setDuration(3500)
        sunRaysRotation.interpolator = LinearInterpolator()
        
        val sunRaysFadeIn = ObjectAnimator.ofFloat(sunRaysView, "alpha", 0f, 1f)
            .setDuration(1800)
        sunRaysFadeIn.startDelay = 1500
        
        val sunRaysFadeOut = ObjectAnimator.ofFloat(sunRaysView, "alpha", 1f, 0f)
            .setDuration(1000)
        sunRaysFadeOut.startDelay = 3000
        
        val sunRaysPulse = ObjectAnimator.ofFloat(sunRaysView, "scaleX", 0.5f, 1f, 1.5f, 1.2f, 1f)
            .setDuration(3500)
        val sunRaysPulseY = ObjectAnimator.ofFloat(sunRaysView, "scaleY", 0.5f, 1f, 1.5f, 1.2f, 1f)
            .setDuration(3500)
        
        // Sun reflection with growing effect
        val sunReflectionY = ObjectAnimator.ofFloat(sunReflection, "translationY", 80f, 0f)
            .setDuration(3500)
        sunReflectionY.interpolator = DecelerateInterpolator(2f)
        
        val sunReflectionAlpha = ObjectAnimator.ofFloat(sunReflection, "alpha", 0f, 0.2f, 0.6f, 0.4f, 0.6f)
            .setDuration(3500)
        
        val sunReflectionScale = ObjectAnimator.ofFloat(sunReflection, "scaleY", 0.3f, 0.8f)
            .setDuration(3500)
        
        // Moon reflection sets
        val moonReflectionSet = ObjectAnimator.ofFloat(moonReflection, "translationY", 0f, 100f)
            .setDuration(1500)
        val moonReflectionFade = ObjectAnimator.ofFloat(moonReflection, "alpha", 0.5f, 0f)
            .setDuration(1500)
        val moonReflectionAnimSet = AnimatorSet().apply {
            playTogether(moonReflectionSet, moonReflectionFade)
        }
        
        // Star reflections disappear
        val starReflectionAnimators = starReflections.map { starReflection ->
            ObjectAnimator.ofFloat(starReflection, "alpha", 0.4f, 0f).setDuration(800)
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(sunriseSkyAnimator)
            .before(heightAnimator)
        animatorSet.play(heightAnimator)
            .with(sunRotation)
            .with(sunScaleX)
            .with(sunScaleY)
            .with(sunAlpha)
            .with(daySkyAnimator)
            .with(sunReflectionY)
            .with(sunReflectionAlpha)
            .with(sunReflectionScale)
            .with(sunRaysRotation)
            .with(sunRaysPulse)
            .with(sunRaysPulseY)

        starAnimators.forEach { it.start() }
        starReflectionAnimators.forEach { it.start() }
        moonAnimSet.start()
        moonReflectionAnimSet.start()
        cloudAnimators.forEach { it.start() }

        animatorSet.start()
        sunRaysFadeIn.start()
        sunRaysFadeOut.start()

        animatorSet.doOnEnd {
            isAnimating = false
            isDaytime = true
            sunView.alpha = 1f // Reset alpha
            moonView.translationY = 0f // Reset moon position
            moonReflection.translationY = 0f // Reset moon reflection position
        }
    }

    private fun AnimatorSet.doOnEnd(action: () -> Unit) {
        this.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                action()
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
    }
    
    private fun startWaveAnimation() {
        // Create continuous wave animations with different speeds and patterns
        waves.forEachIndexed { index, wave ->
            val duration = 2000L + index * 500L
            val translationY = 20f + index * 10f
            
            // Wave up and down motion
            val waveMotion = ObjectAnimator.ofFloat(wave, "translationY", -translationY, translationY)
                .setDuration(duration)
            waveMotion.repeatCount = ObjectAnimator.INFINITE
            waveMotion.repeatMode = ObjectAnimator.REVERSE
            waveMotion.interpolator = LinearInterpolator()
            waveMotion.startDelay = index * 300L
            
            // Wave alpha pulsing
            val waveAlpha = ObjectAnimator.ofFloat(wave, "alpha", 0.2f + index * 0.05f, 0.4f + index * 0.05f)
                .setDuration(duration)
            waveAlpha.repeatCount = ObjectAnimator.INFINITE
            waveAlpha.repeatMode = ObjectAnimator.REVERSE
            waveAlpha.startDelay = index * 300L
            
            waveMotion.start()
            waveAlpha.start()
        }
    }
    
    private fun animateReflections(
        sunY: Float,
        sunAlpha: Float = 1f,
        showMoon: Boolean = false,
        showStars: Boolean = false
    ) {
        // Animate sun reflection to follow sun position
        val reflectionY = 20f + (sunY / skyView.height) * 30f
        ObjectAnimator.ofFloat(sunReflection, "translationY", reflectionY)
            .setDuration(100)
            .start()
        
        // Update sun reflection alpha with wave distortion
        val reflectionAlpha = (sunAlpha * 0.6f).coerceIn(0f, 0.6f)
        ObjectAnimator.ofFloat(sunReflection, "alpha", reflectionAlpha)
            .setDuration(100)
            .start()
        
        // Reflection scale based on sun position (gets smaller as sun sets)
        val reflectionScale = 0.8f - (sunY / skyView.height) * 0.3f
        ObjectAnimator.ofFloat(sunReflection, "scaleY", reflectionScale)
            .setDuration(100)
            .start()
    }
}
