package com.example.deliveryapp.ui.signup


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.deliveryapp.R
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.databinding.ActivitySignUpBinding
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.utils.EspressoTestingIdlingResource
import dagger.android.AndroidInjection
import dagger.hilt.android.AndroidEntryPoint
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        binding.signupButton.setOnClickListener {
            val name = binding.signupNameEditext.text.toString()
            val phone = binding.signupPhoneEditext.text.toString()
            val email = binding.signupEmailEditext.text.toString()
            val password = binding.signupPasswordEditext.text.toString()
            val confirmPassword = binding.signupConfirmPasswordEditext.text.toString()

            Timber.d("$name, $phone, $email, $password, $confirmPassword")
            validateAndSignUpUser(name, phone, email, password, confirmPassword)
        }
    }

    private fun validateAndSignUpUser(name:String, phone:String, email:String, password:String, confirmPassword: String){

        val valMap = viewModel.validateSignUpDetails(name, phone, email, password, confirmPassword)

        processValidationMap(valMap)

    }

    private fun handleNetworkState(networkState: NetworkState) {
        when (networkState.status) {
            Status.SUCCESS -> {
                goToNextActivity()
                EspressoTestingIdlingResource.decrement()
            }
            Status.FAILED -> {
                binding.signupButton.visibility = View.VISIBLE
                EspressoTestingIdlingResource.decrement()
            }
            else -> {}
        }
    }

    private fun goToNextActivity(){
        startActivity(LoginActivity.newInstance(this,MainActivity.SALUTATION_TYPE_SIGN_UP))
    }

    private fun processValidationMap(valMap: HashMap<String, Int>){
        resetTextInputLayoutErrors()

        when {
            valMap[SignUpViewModel.VAL_MAP_NAME_KEY]!= SignUpViewModel.VAL_VALID -> binding.signupNameTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_NAME_KEY]!!)
            valMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!= SignUpViewModel.VAL_VALID -> binding.signupPhoneTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!!)
            valMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!= SignUpViewModel.VAL_VALID -> binding.signupEmailTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!!)
            valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY] != SignUpViewModel.VAL_VALID -> {

                binding.signupPasswordTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!)

                if(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY] == SignUpViewModel.PASSWORDS_DONT_MATCH){
                    binding.signupConfirmPasswordTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!)
                }

            }
            else -> {

                binding.signupButton.visibility = View.GONE
                viewModel.signUpUser(binding.signupNameEditext.text.toString(),
                    binding.signupPhoneEditext.text.toString(),
                    binding.signupEmailEditext.text.toString(),
                    binding.signupPasswordEditext.text.toString())

                EspressoTestingIdlingResource.increment()
                viewModel.networkState.observe(this, { networkState->
                    binding.networkState = networkState
                    handleNetworkState(networkState)
                })
            }
        }
    }

    private fun resetTextInputLayoutErrors(){

        binding.signupNameTextLayout.error = null
        binding.signupPhoneTextLayout.error = null
        binding.signupEmailTextLayout.error = null
        binding.signupConfirmPasswordTextLayout.error = null
        binding.signupPasswordTextLayout.error = null
    }


}
