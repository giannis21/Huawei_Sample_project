package com.example.huawei_sample_project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var searchService: SearchService? = null

    var resultTextView: TextView? = null

    var queryInput: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            searchService = SearchServiceFactory.create(this, URLEncoder.encode("AIzaSyC-XJqL7ohRsOasFjOyMv25bgBgvHHQVec", "utf-8"))
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "encode apikey error")
        }

        queryInput = findViewById<EditText>(R.id.edit_text_text_search_query)
        resultTextView = findViewById<TextView>(R.id.response_text_search)
    }
    fun search(view: View?) {
        val textSearchRequest = TextSearchRequest()
        textSearchRequest.query = queryInput?.text.toString()
        searchService?.textSearch(
                textSearchRequest,
                object : SearchResultListener<TextSearchResponse> {
                    override fun onSearchResult(textSearchResponse: TextSearchResponse?) {
                        val siteList: List<Site>? = textSearchResponse?.getSites()
                        if (textSearchResponse == null || textSearchResponse.getTotalCount() <= 0 || siteList.isNullOrEmpty()) {
                            resultTextView?.text = "Result is Empty!"
                            return
                        }
                        val response = StringBuilder("\nsuccess\n")
                        var addressDetail: AddressDetail?

                        textSearchResponse.sites.forEachIndexed {index, site ->
                            addressDetail = site.address

                            response.append("[${index + 1}]  name: ${site.name}, formatAddress: ${site.formatAddress}, country: ${addressDetail?.country ?: ""}, countryCode: ${addressDetail?.countryCode ?: ""} \r\n")

                        }

                        Log.d(TAG, "search result is : $response")
                        resultTextView?.text = response.toString()
                    }

                    override fun onSearchError(searchStatus: SearchStatus) {
                        Log.e(TAG, "onSearchError is: " + searchStatus.errorCode)
                        resultTextView?.text = "Error : ${searchStatus.getErrorCode()}  ${searchStatus.getErrorMessage()}"
                    }
                })
    }
}