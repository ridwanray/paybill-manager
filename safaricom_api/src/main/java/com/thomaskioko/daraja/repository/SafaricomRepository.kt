package com.thomaskioko.daraja.repository

import android.arch.lifecycle.LiveData
import com.thomaskioko.daraja.repository.api.util.ApiResponse
import com.thomaskioko.daraja.repository.api.interceptor.SafaricomAuthInterceptor
import com.thomaskioko.daraja.repository.api.service.SafaricomService
import com.thomaskioko.daraja.repository.api.service.SafaricomTokenService
import com.thomaskioko.daraja.repository.api.util.AppExecutors
import com.thomaskioko.daraja.repository.api.util.NetworkBoundResource
import com.thomaskioko.daraja.repository.api.util.Resource
import com.thomaskioko.daraja.repository.api.util.livedata.AbsentLiveData
import com.thomaskioko.daraja.repository.db.dao.SafaricomPushRequestDao
import com.thomaskioko.daraja.repository.db.dao.SafaricomTokenDao
import com.thomaskioko.daraja.repository.db.entity.SafaricomPushRequest
import com.thomaskioko.daraja.repository.db.entity.SafaricomToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafaricomRepository @Inject
constructor(private val mAppExecutors: AppExecutors, private val mSafaricomService: SafaricomService,
            private val mSafaricomTokenService: SafaricomTokenService, private val mSafaricomTokenDao: SafaricomTokenDao,
            private val mSafaricomPushRequestDao: SafaricomPushRequestDao,
            private val mSafaricomAuthInterceptor: SafaricomAuthInterceptor) {


    val accessToken: LiveData<Resource<SafaricomToken>>
        get() = object : NetworkBoundResource<SafaricomToken, SafaricomToken>(mAppExecutors) {
            private var safaricomToken: SafaricomToken? = null

            override fun saveCallResult(item: SafaricomToken) {

                mSafaricomTokenDao.insert(item)

                mSafaricomAuthInterceptor.setAuthToken(item.accessToken)
                if (mSafaricomTokenDao.getAccessToken().value != null) {
                    safaricomToken = mSafaricomTokenDao.getAccessToken().value
                }
            }

            override fun shouldFetch(data: SafaricomToken?): Boolean {

                //TODO:: check if the time has expired
                return true
            }

            override fun loadFromDb(): LiveData<SafaricomToken> {
                return if (safaricomToken == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<SafaricomToken>() {
                        override fun onActive() {
                            super.onActive()
                            value = safaricomToken
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<SafaricomToken>> {
                return mSafaricomTokenService.accessToken
            }
        }.asLiveData()

    fun sendPaymentRequest(safaricomPushRequest: SafaricomPushRequest): LiveData<Resource<SafaricomPushRequest>> {
        return object : NetworkBoundResource<SafaricomPushRequest, SafaricomPushRequest>(mAppExecutors) {
            // Temp ResultType
            private var safaricomToken: SafaricomPushRequest? = null

            override fun saveCallResult(item: SafaricomPushRequest) {
                mSafaricomPushRequestDao.insert(item)

                if (mSafaricomPushRequestDao.findAll().value != null) {
                    safaricomToken = mSafaricomPushRequestDao.findAll().value!![0]
                }
            }

            override fun shouldFetch(data: SafaricomPushRequest?): Boolean {
                //Always fetch data
                return true
            }

            override fun loadFromDb(): LiveData<SafaricomPushRequest> {
                //Fetch from the db
                return if (safaricomToken == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<SafaricomPushRequest>() {
                        override fun onActive() {
                            super.onActive()
                            value = safaricomToken
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<SafaricomPushRequest>> {
                return mSafaricomService.sendPush(safaricomPushRequest)
            }
        }.asLiveData()
    }
}