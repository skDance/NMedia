package ru.netology.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.api.PostApi
import ru.netology.dto.Post

class PostRepositoryImpl : PostRepository {

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        PostApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onFailure(Exception(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: emptyList())
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onFailure(Exception(t))
            }

        })


//        val request: Request = Request.Builder()
//            .url("${BASE_URL}/api/slow/posts")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onFailure(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (!response.isSuccessful) {
//                        callback.onFailure(Exception(response.message))
//                        return
//                    }
//                    try {
//                        val data = response.body?.string()
//                            ?: throw java.lang.RuntimeException("body is null")
//                        callback.onSuccess(gson.fromJson(data, typeToken))
//                    } catch (e: Exception) {
//                        callback.onFailure(e)
//                    }
//                }
//            })
    }

    override fun likeById(post: Post, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.likeById(post.id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onFailure(Exception(response.message()))
                    return
                }
                callback.onSuccess(response.body()!!)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onFailure(Exception(t))
            }
        })

//        val request: Request = if (post.likedByMe) {
//            Request.Builder()
//                .delete()
//                .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
//                .build()
//        } else {
//            Request.Builder()
//                .post("".toRequestBody(jsonType))
//                .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
//                .build()
//        }
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onFailure(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (!response.isSuccessful) {
//                        callback.onFailure(Exception(response.message))
//                        return
//                    }
//
//                    val data =
//                        response.body?.string() ?: throw java.lang.RuntimeException("body is null")
//                    callback.onSuccess(gson.fromJson(data, Post::class.java))
//                }
//            })
    }

    override fun shareById(id: Long) {
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.removeById(id).enqueue(object: Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onFailure(Exception(response.message()))
                    return
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onFailure(Exception(t))
            }

        })



//        val request: Request = Request.Builder()
//            .delete()
//            .url("${BASE_URL}/api/slow/posts/$id")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onFailure(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (!response.isSuccessful) callback.onFailure(Exception(response.message))
//                }
//            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.save().enqueue(object: Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onFailure(Exception(response.message()))
                    return
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onFailure(Exception(t))
            }

        })





//        val request: Request = Request.Builder()
//            .post(gson.toJson(post).toRequestBody(jsonType))
//            .url("${BASE_URL}/api/slow/posts")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onFailure(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (!response.isSuccessful) callback.onFailure(Exception(response.message))
//                }
//            })
    }
}