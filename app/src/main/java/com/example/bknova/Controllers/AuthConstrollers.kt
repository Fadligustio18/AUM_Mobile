package com.example.bknova.Controllers

import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import com.example.bknova.service.Aktor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthConstrollers {
    private val service = Aktor.auth
    suspend fun loginControllers( login: Login): LoginFeedback?= withContext(Dispatchers.IO){
        try{
            val response = service.Login_Services(login).execute()
            if(response.code()==200){
                return@withContext response.body()
            }else{
                return@withContext null
            }
        }catch (e:Exception){
            return@withContext null
        }
    }
}