package com.rampu.erasmapp.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rampu.erasmapp.auth.domain.AuthResult
import com.rampu.erasmapp.auth.domain.FailureReason
import com.rampu.erasmapp.auth.domain.IAuthRepository
import com.rampu.erasmapp.auth.domain.UserAccount
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthImplementation (private val auth: FirebaseAuth) : IAuthRepository {

    override val authState: Flow<UserAccount?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener{ firebaseAuth ->
            val user = firebaseAuth.currentUser

            trySend(
                if(user == null) null
                else UserAccount(
                    uid = user.uid,
                    email = user.email,
                )
            )
        }

        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }


    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email,password).await()
            val user = result.user ?: return AuthResult.Failure(
                reason = FailureReason.OTHER,
                message = "User was null")

            AuthResult.Success(
                user = UserAccount(
                    uid = user.uid,
                    email = user.email,
                )
            )

        } catch (e: CancellationException){
            throw e
        } catch (e: Exception){
            val reason = FirebaseAuthFailureMapper.mapToFailureReason(e)
            val message = FirebaseAuthFailureMapper.niceMessage(reason)
            AuthResult.Failure(
                reason = reason,
                message = message
            )
        }

    }

    override suspend fun register(email: String, password: String): AuthResult {
        return try{
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Failure(
                reason = FailureReason.OTHER,
                message = "User was null")
            
            AuthResult.Success(
                user = UserAccount(
                    uid = user.uid,
                    email = user.email,
                )
            )

        } catch (e: CancellationException){
            throw e
        } catch (e: Exception){
            val reason = FirebaseAuthFailureMapper.mapToFailureReason(e)
            val message = FirebaseAuthFailureMapper.niceMessage(reason)
            AuthResult.Failure(
                reason = reason,
                message = message
            )
        }
    }

    override suspend fun signOut() = auth.signOut()

    override suspend fun signInWithGoogle(idToken: String) : AuthResult {
        return try{
            val credential = GoogleAuthProvider.getCredential(idToken,null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return AuthResult.Failure(
                reason = FailureReason.OTHER,
                message = "user was null")

            AuthResult.Success(
                user = UserAccount(
                    uid = user.uid,
                    email = user.email
                )
            )

        } catch (e: CancellationException){
            throw e
        }catch (e: Exception){
            val reason = FirebaseAuthFailureMapper.mapToFailureReason(e)
            val message = FirebaseAuthFailureMapper.niceMessage(reason)
            AuthResult.Failure(
                reason = reason,
                message = message
            )
        }
    }

}